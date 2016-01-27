package scheduler

import calculate.stock.RateOfReturnStrategy
import config.{SparkConfig, StrategyConfig}
import log.URLogger
import org.apache.spark.{SparkConf, SparkContext}
import stock.Stock
import util.{FileUtil, TimeUtil}

import scala.collection.mutable

/**
  * Created by yangshuai on 2016/1/16.
  * 主流程
  */
object Scheduler {

  var prePriceMap = new mutable.HashMap[String, Stock]()
  var stockReturnMap = new mutable.HashMap[String, Float]()
  var preUserMap = mutable.Map[String, Set[String]]()
  var newStockMap = mutable.Map[String, Set[String]]()

  val OPEN_MARKET_HOUR = 9
  val CLOSE_MARKET_HOUR = 15

  val conf =  new SparkConf().setMaster("local").setAppName("USER RANK").set("spark.serializer", SparkConfig.SPARK_SERIALIZER).set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    URLogger.warn("Begin to calculate rate of return.")

    try {

      //计算昨日9点对应于今日15点的股票的回报率
      prePriceMap = FileUtil.readStockCodeByDayAndHour(-1, OPEN_MARKET_HOUR)
      URLogger.warn("Pre size: " + prePriceMap.size)
      val currentPrice = FileUtil.readStockCodeByDayAndHour(0, CLOSE_MARKET_HOUR)
      URLogger.warn("Current size: " + currentPrice.size)
      val rateOfReturnArr = sc.parallelize(currentPrice.toSeq).map(x => getRateOfReturn(x._1, x._2)).filter(_.length > 0).collect
      URLogger.warn("Rate of return number: " + rateOfReturnArr.length)
      FileUtil.writeRateOfReturnStrategyOneFile(rateOfReturnArr, TimeUtil.getPreWorkDay(-1), TimeUtil.getDay)

      //计算用户回报率
      preUserMap = FileUtil.readUserInfoByDayAndHour(-3, 15)
      val userMap = FileUtil.readUserInfoByDayAndHour(-2, 9)
      val userRank = sc.parallelize(userMap.toSeq).filter(_._2.nonEmpty).map(x => getReturn(x._1, x._2)).filter(_._3 != "empty").sortBy(_._2, ascending = false).map(x => x._1 + "\t" + x._2 + "\t" + x._3).collect
      FileUtil.saveUserRank(userRank, userMap.size / 5)

    } catch {
      case e: Exception =>
        URLogger.exception(e)
    } finally {
      sc.stop()
    }
  }

  /**
    * 计算单个用户的回报率
    */
  def getReturn(userId: String, set: Set[String]): (String, Float, String) = {

    var result = 0f
    var size = 0

    val preSet = preUserMap.get(userId)
    if (preSet.isEmpty)
      return (userId + "\t0", 0f, "")

    val remainSet = set.--(preSet.get)

    if (remainSet.isEmpty)
      return (userId + "\t0", 0f, "empty")

    for (code <- remainSet) {
      val value = stockReturnMap.get(code)

      if (value.isDefined) {
        result += value.get
        size += 1
      }
    }

    newStockMap.put(userId, remainSet)

    if (size == 0)
      return (userId + "\t0", 0f, "")

    (userId + "\t" + result, result / size, convertSetToStringSplitBy(remainSet, ","))
  }

  /**
    * convert set to string split by assigned string
    */
  def convertSetToStringSplitBy(set: Set[String], spliter: String): String = {

    var str = ""

    for (ele <- set) {
      str += ele + ","
    }

    str.substring(0, str.length - 1)
  }

  /**
    * 计算回报率
    */
  def getRateOfReturn(code: String, stock: Stock): String = {
    val pre = prePriceMap.get(code)
    if (pre.isEmpty || pre.get == null) {
      code + "没有前一天的对应价格"
    } else {
      val rate = RateOfReturnStrategy.apply(StrategyConfig.STRATEGY_ONE).calculate(pre.get, stock).current_rate
      stockReturnMap.put(code.substring(2), rate)
      code + "\t" + rate.toString
    }
  }
}
