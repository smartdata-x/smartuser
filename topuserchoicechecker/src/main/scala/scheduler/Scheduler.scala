package scheduler

import config.SparkConfig
import log.TUCLogger
import org.apache.spark.{SparkConf, SparkContext}
import stock.Stock
import util.FileUtil

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/2/16.
  */
object Scheduler {

  var preUserMap = mutable.Map[String, Set[String]]()
  var topUsers = mutable.ListBuffer[String]()

  val OPEN_MARKET_HOUR = 9
  val CLOSE_MARKET_HOUR = 15

  var openStockPrice = mutable.Map[String, Stock]()
  var closeStockPrice = mutable.Map[String, Stock]()

  val conf =  new SparkConf().setMaster("local").setAppName("TOP USER CHOICE CHECKER").set("spark.serializer", SparkConfig.SPARK_SERIALIZER).set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    topUsers = FileUtil.readTopUserList(args(0).toInt)
    TUCLogger.warn("Top user number: " + topUsers.size)
    preUserMap = FileUtil.readUserInfoByDayAndHour(args(0).toInt, 15)
    val curUserMap = FileUtil.readUserInfoByDayAndHour(args(1).toInt, 9)
    // openStockPrice = FileUtil.readStockCodeByDayAndHour(0, OPEN_MARKET_HOUR)
    TUCLogger.warn("openStockPrice size: " + openStockPrice.size)
    closeStockPrice = FileUtil.readStockCodeByDayAndHour(args(2).toInt, CLOSE_MARKET_HOUR)
    TUCLogger.warn("closeStockPrice size: " + closeStockPrice.size)
    // 增加当天股票价格的checker
    val result = sc.parallelize(curUserMap.toSeq).filter(_._2.nonEmpty).flatMap(x => getNewStocks(x._1, x._2)).filter(filterA).map((_,1)).reduceByKey(_+_).sortBy(_._2, ascending = false)
      .map(x => x._1 + "\t" + (x._2 * 1.0 / topUsers.size) + "\t" + getStockCodePriceChecker(x._1)).collect
    FileUtil.saveUserChoiceChecker(result,args(1).toInt)
    sc.stop
  }

  def getNewStocks(userId: String, set: Set[String]): Set[String] = {

    if (!topUsers.contains(userId))
      return Set[String]()

    val preSet = preUserMap.get(userId)
    if (preSet.isEmpty)
      return Set[String]()

    set.--(preSet.get)
  }
  def filterA(code:String): Boolean ={
    val head = code.charAt(0)
    if (head == '0' || head == '3'|| head == '6' || head == '9') {
      true
    } else{
      false
    }
  }
  def getStockCodePriceChecker(stockCode:String):String = {
    val validStockCode = Stock.getTypeOfStockCode(stockCode)
    if(closeStockPrice.contains(validStockCode)) {
      val todayOpenPrice = closeStockPrice.get (validStockCode).get.todayOpeningPrice
      val currentPrice = closeStockPrice.get (validStockCode).get.currentPrice
      var priceCheck = "stop"
      if (todayOpenPrice != 0) {
        priceCheck = ((currentPrice - todayOpenPrice) * 1.0 / todayOpenPrice).toString
      }
      priceCheck

    }else{
      TUCLogger.warn(validStockCode +":stockCode not contains in Stock of today>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
      val error = "isNotA"
      error
    }
  }
}
