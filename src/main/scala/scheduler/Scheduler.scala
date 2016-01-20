package scheduler

import java.util.Calendar
import java.util.concurrent.TimeUnit

import analysis.TableHbase
import calculate.stock.RateOfReturnStrategy
import config.{SparkConfig, StrategyConfig}
import log.SULogger
import net.SinaRequest
import org.apache.spark.{SparkConf, SparkContext}
import stock.Stock
import util.HdfsFileUtil

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/16.
  */
object Scheduler {

  var prePriceMap = new mutable.HashMap[String, Stock]()
  var stockList = new ListBuffer[Stock]()

  val taskHour = Array[Int](9, 11, 13, 15)
  val taskMinute = Array[Int](30, 30, 0, 0)
  var taskIndex = 0
  var requesting = false
  var calculating = false

  val conf =  new SparkConf().setMaster("local").setAppName("su").set("spark.serializer", SparkConfig.SPARK_SERIALIZER).set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    while(true) {

      while (!requesting) {

        requesting = true
        val arr = TableHbase.getStockCodesFromHbase(sc, 1)
        if (arr != null)
          SinaRequest.requestStockList(arr.toArray, afterRequest)

      }

      while (calculating) {

        try {
          val currentHour = Calendar.getInstance.get(Calendar.HOUR_OF_DAY)
          //    if (taskIndex % 2 == 1) {
          prePriceMap = HdfsFileUtil.readTodayStockCodeByHour(9)
          SULogger.warn("pre size: " + prePriceMap.size)
          val currentPrice = HdfsFileUtil.readTodayStockCodeByHour(currentHour)
          SULogger.warn("current size: " + currentPrice.size)
          val rateOfReturnArr = sc.parallelize(currentPrice.toSeq).map(x => getRateOfReturn(x._1, x._2)).filter(_.length > 0).collect
          SULogger.warn("rate of return number: " + rateOfReturnArr.length)
          HdfsFileUtil.writeRateOfReturnStrategyOneFile(rateOfReturnArr, 9, currentHour)
          //    } else if (taskIndex == 3) {
        } catch {
          case e: Exception =>
            e.printStackTrace()
        }
        //
        //    }

        calculating = false
        requesting = false

        SULogger.warn("before sleep")
        TimeUnit.SECONDS.sleep(100)
      }

    }
  }

  def afterRequest(): Unit = {
    calculating = true
  }

  /**
    * 计算回报率
    */
  def getRateOfReturn(code: String, stock: Stock): String = {
    val pre = prePriceMap.get(code)
    if (pre.isEmpty || pre.get == null) {
      ""
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
