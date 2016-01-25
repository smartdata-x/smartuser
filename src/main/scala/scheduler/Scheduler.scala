package scheduler

import java.util.Calendar
import java.util.concurrent.TimeUnit

import calculate.stock.RateOfReturnStrategy
import config.{RegExpConfig, SparkConfig, StrategyConfig}
import data.{FileUtil, HbaseUtil}
import log.SULogger
import net.SinaRequest
import org.apache.spark.{SparkConf, SparkContext}
import stock.Stock
import util.TimeUtil

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/16.
  * 主流程
  */
object Scheduler {

  var prePriceMap = new mutable.HashMap[String, Stock]()
  var stockReturnMap = new mutable.HashMap[String, Float]()
  var stockList = new ListBuffer[Stock]()
  var userMap = new mutable.HashMap[String, ListBuffer[String]]()

  var topUser = Set[String]()

  val taskHour = Array[Int](9, 11, 13, 15)
  val taskMinute = Array[Int](30, 30, 0, 0)
  var taskIndex = 0

  var requesting = false
  var calculating = false
  var writing = false

  val conf =  new SparkConf().setMaster("local").setAppName("su").set("spark.serializer", SparkConfig.SPARK_SERIALIZER).set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    while(true) {

      //定时器
      if (!requesting && !writing && !calculating) {
        SULogger.warn("Before sleep")
        if (args.length == 0) {
          Timer.waitToNextTask()
        } else {
          TimeUnit.SECONDS.sleep(10)
        }
      }

      //请求股票信息
      if (!requesting) {

        requesting = true
        userMap.clear
        stockList.clear

        val arr = HbaseUtil.getStockCodes(sc, 1)
        SULogger.warn("array length: " + arr.length)
        SinaRequest.requestStockList(arr, afterRequest)
      }

      //保存用户自选股信息
      if (writing) {
        FileUtil.saveUserStockInfo()
        writing = false
        requesting = false
      }

      //计算回报率
      if (calculating) {

        SULogger.warn("Begin to calculate rate of return.")

        try {

          val currentHour = Calendar.getInstance.get(Calendar.HOUR_OF_DAY)

          //计算当前时间与上午9点的回报率
          prePriceMap = FileUtil.readTodayStockCodeByHour(9)
          SULogger.warn("Pre size: " + prePriceMap.size)
          val currentPrice = FileUtil.readTodayStockCodeByHour(currentHour)
          SULogger.warn("Current size: " + currentPrice.size)
          var rateOfReturnArr = sc.parallelize(currentPrice.toSeq).map(x => getRateOfReturn(x._1, x._2)).filter(_.length > 0).collect
          SULogger.warn("Rate of return number: " + rateOfReturnArr.length)
          FileUtil.writeRateOfReturnStrategyOneFile(rateOfReturnArr, 9, currentHour)

          stockReturnMap = getReturnMap(rateOfReturnArr)
          val userReturnArr = sc.parallelize(userMap.toSeq).map(x => getReturn(x._1, x._2)).filter(_._1.length > 0).sortBy(_._2, ascending = false).map(x => x._1 + "\t" + x._2).collect
          SULogger.warn("User number: " + userReturnArr.length)
          FileUtil.saveUserReturnInfo(userReturnArr, "9-" + currentHour)

          //计算15点与13点的回报率
          if (TimeUtil.getCurrentHour == 15) {

            prePriceMap = FileUtil.readTodayStockCodeByHour(13)
            rateOfReturnArr = sc.parallelize(currentPrice.toSeq).map(x => getRateOfReturn(x._1, x._2)).filter(_.length > 0).collect
            FileUtil.writeRateOfReturnStrategyOneFile(rateOfReturnArr, 13, currentHour)

            stockReturnMap = getReturnMap(rateOfReturnArr)
            val userReturnArr = sc.parallelize(userMap.toSeq).map(x => getReturn(x._1, x._2)).sortBy(_._2, ascending = false).map(x => x._1 + "\t" + x._2).collect
            FileUtil.saveUserReturnInfo(userReturnArr, "13-" + currentHour)

            topUser = sc.parallelize(userMap.toSeq).map(x => getReturn(x._1, x._2)).sortBy(_._2, ascending = false).map(_._1).take(100).toSet
            val stockRank = sc.parallelize(userMap.toSeq).filter(x => ifTopUser(x._1)).flatMap(_._2).map((_, 1)).reduceByKey(_+_).sortBy(_._2, ascending = false).map(x => x._1 + "\t" + x._2).collect
            FileUtil.saveStockRankInfo(stockRank, "13-" + currentHour)
//            val rateOfStockFocusChanging =
          }
        } catch {
          case e: Exception =>
            e.printStackTrace()
        } finally {
          calculating = false
        }
      }
    }
  }

  def getRateOfStockFocusChanging(infos: Seq[String], fileName: String): Seq[String] = {

//    val list = FileUtil.readFile()
    null
  }

  def ifTopUser(userId: String): Boolean = {
    if (topUser.contains(userId)) {
      return true
    }
    false
  }

  /**
    * 计算单个用户的回报率
    */
  def getReturn(userId: String, list: Seq[String]): (String, Float) = {

    var result = 0f
    var size = 0

    for (code <- list) {
      val rate = stockReturnMap.get(code)
      if (rate.isDefined) {
        size += 1
        result += rate.get
      }
    }

    (userId, result / size)
  }


  /**
    * 获得("股票代码", "回报率")
    */
  def getReturnMap(arr: Array[String]): mutable.HashMap[String, Float] = {

    val returnMap = new mutable.HashMap[String, Float]()

    for(str <- arr) {
      val temp = str.split("\t")
      if (temp(1).matches(RegExpConfig.FLOAT)) {
        returnMap.put(temp(0).substring(2), temp(1).toFloat)
      }
    }

    returnMap
  }

  def afterRequest(): Unit = {
    SULogger.warn("After request")
    writing = true
    val hour = TimeUtil.getCurrentHour
//    if (hour == 11 || hour == 15)
      calculating = true
  }

  /**
    * 计算回报率
    */
  def getRateOfReturn(code: String, stock: Stock): String = {
    val pre = prePriceMap.get(code)
    if (pre.isEmpty || pre.get == null) {
      ""
    } else if (stock.currentPrice == 0) {
      code + "\t" + "停牌"
    } else if (pre.get.currentPrice == 0) {
      code + "\t" + "午后复牌"
    } else {
      val rate = RateOfReturnStrategy.apply(StrategyConfig.STRATEGY_ONE).calculate(pre.get, stock).current_rate
      code + "\t" + rate.toString
    }
  }

  /**
    * 是否开始任务
    */
  def begin(): Boolean = {

    TimeUnit.SECONDS.sleep(10)

    val calendar = Calendar.getInstance
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    if (hour >= taskHour(taskIndex) && minute >= taskMinute(taskIndex)) {
      if (taskIndex == taskHour.length - 1) {
        taskIndex = 0
      } else {
        taskIndex += 1
      }

      return true
    }
    false
  }
}
