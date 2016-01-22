package scheduler

import java.util.Calendar
import java.util.concurrent.TimeUnit
import calculate.stock.RateOfReturnStrategy
import config.{SparkConfig, StrategyConfig}
import data.{HbaseUtil, FileUtil}
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
  var stockList = new ListBuffer[Stock]()
  val userMap = new mutable.HashMap[String, ListBuffer[String]]()

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
        SULogger.warn("before sleep")
        if (args.length == 0) {
          Timer.waitToNextTask()
        } else {
          TimeUnit.SECONDS.sleep(100)
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
          SULogger.warn("pre size: " + prePriceMap.size)
          val currentPrice = FileUtil.readTodayStockCodeByHour(currentHour)
          SULogger.warn("current size: " + currentPrice.size)
          var rateOfReturnArr = sc.parallelize(currentPrice.toSeq).map(x => getRateOfReturn(x._1, x._2)).filter(_.length > 0).collect
          SULogger.warn("rate of return number: " + rateOfReturnArr.length)
          FileUtil.writeRateOfReturnStrategyOneFile(rateOfReturnArr, 9, currentHour)

          //计算15点与13点的回报率
          if (TimeUtil.getCurrentHour() == 15) {
            prePriceMap = FileUtil.readTodayStockCodeByHour(13)
            rateOfReturnArr = sc.parallelize(currentPrice.toSeq).map(x => getRateOfReturn(x._1, x._2)).filter(_.length > 0).collect
            FileUtil.writeRateOfReturnStrategyOneFile(rateOfReturnArr, 13, currentHour)
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

  def afterRequest(): Unit = {
    writing = true
    val hour = TimeUtil.getCurrentHour()
    if (hour == 11 || hour == 15)
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
      val rate = RateOfReturnStrategy.apply(StrategyConfig.STRATEGY_ONE).calculate(pre.get, stock).getRateOfReturn
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
