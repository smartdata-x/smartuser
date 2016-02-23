package scheduler

import config.SparkConfig
import log.TUCLogger
import message.SendMessage
import org.apache.spark.{SparkConf, SparkContext}
import stock.Stock
import util.FileUtil

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

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

  var userChoiceChecker = ListBuffer[String]()

  val conf =  new SparkConf().setMaster("local").setAppName("TOP USER CHOICE CHECKER")
    .set("spark.serializer", SparkConfig.SPARK_SERIALIZER).set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
    .set("spark.driver.allowMultipleContexts","true")
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    topUsers = FileUtil.readTopUserList(args(0).toInt)
    if(topUsers.isEmpty){
      SendMessage.sendMessage(1,"聪明账户", "当天Rank_User的信息获取异常")
      System.exit(-1)
    }
    TUCLogger.warn("Top user number: " + topUsers.size)
    preUserMap = FileUtil.readUserInfoByDayAndHour(args(0).toInt, 15)
    if(preUserMap.isEmpty){
      SendMessage.sendMessage(1,"聪明账户", "昨日15时用户信息获取异常")
      System.exit(-1)
    }
    val curUserMap = FileUtil.readUserInfoByDayAndHour(args(1).toInt, 9)
    if(curUserMap.isEmpty){
      SendMessage.sendMessage(1,"聪明账户", "今日9时用户信息获取异常")
      System.exit(-1)
    }
    // openStockPrice = FileUtil.readStockCodeByDayAndHour(0, OPEN_MARKET_HOUR)
    TUCLogger.warn("openStockPrice size: " + openStockPrice.size)
    closeStockPrice = FileUtil.readStockCodeByDayAndHour(args(2).toInt, CLOSE_MARKET_HOUR)
    if(closeStockPrice.isEmpty){
      SendMessage.sendMessage(1,"聪明账户", "今日大盘信息获取异常")
      System.exit(-1)
    }
    TUCLogger.warn("closeStockPrice size: " + closeStockPrice.size)
    // 增加当天股票价格的checker
    val result = sc.parallelize(curUserMap.toSeq).filter(_._2.nonEmpty).flatMap(x => getNewStocks(x._1, x._2)).filter(filterA).map((_,1)).reduceByKey(_+_).sortBy(_._2, ascending = false)
      .map(x => x._1 + "\t" + (x._2 * 1.0 / topUsers.size) + "\t" + getStockCodePriceChecker(x._1)).collect
    FileUtil.saveUserChoiceChecker(result,args(1).toInt)

    userChoiceChecker = FileUtil.readChoiceCheckerByDay(args(1).toInt,15)
    TUCLogger.warn("userChoiceChecker size: " + userChoiceChecker.size)
    val top10 = sc.parallelize(userChoiceChecker.toSeq).map(getUserChoiceCheck).keyBy(_._2.toFloat).sortByKey(false).top(10).filter(x => filterPositive(x._2._3))
      // .foreach(x => println("SSS:"+x))
    TUCLogger.warn("top10 size: " + top10.size)
    val top100 = sc.parallelize(userChoiceChecker.toSeq).map(getUserChoiceCheck).keyBy(_._2.toFloat).sortByKey(false).top(100).filter(x => filterPositive(x._2._3))
    TUCLogger.warn("top100 size: " + top100.size)
    FileUtil.saveUserChoiceCheckerPercent((top10.size * 1.0 / 10 ).toString,args(1).toInt,10)
    FileUtil.saveUserChoiceCheckerPercent((top100.size * 1.0 / 100 ).toString,args(1).toInt,100)
    sc.stop
  }

  def getNewStocks(userId: String, set: Set[String]): Set[String] = {

    if (!topUsers.contains(userId))
      return Set[String]()

    val preSet = preUserMap.get(userId)
    if (preSet.isEmpty)
      return Set[String]()

    set.-- (preSet.get)
  }

  /**
    * 过滤不是A股股票
    * @param code
    * @return
    *@author C.J.YOU
    */
  def filterA(code:String): Boolean ={
    val head = code.charAt(0)
    if (head == '0' || head == '3'|| head == '6' || head == '9') {
      true
    } else{
      false
    }
  }

  /**
    * 获取当天股票的涨幅值
    * @param stockCode
    * @return
    * @author C.J.YOU
    */
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
      TUCLogger.warn(validStockCode +":stockCode not contains in Stock of today >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
      val error = "stop"
      error
    }
  }

  def getUserChoiceCheck(str:String): (String,String,String) ={

    val strArray = str.split("\t")
    val stockCode = strArray(0)
    val userChoice = strArray(1)
    val choiceCheck = strArray(2)
    (stockCode,userChoice,choiceCheck)
  }

  def filterPositive(choiceChecker:String): Boolean ={

    if(choiceChecker == "stop") false
    else if(choiceChecker.startsWith("-")) false
    else if (choiceChecker.toFloat == 0.0) false
    else true
  }
}
