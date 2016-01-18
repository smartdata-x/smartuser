package scheduler

import java.util.Calendar
import java.util.concurrent.TimeUnit

import analysis.TableHbase
import calculate.RateOfReturnStrategy
import config.{StrategyConfig, SparkConfig}
import log.SULogger
import net.SinaRequest
import org.apache.spark.{SparkConf, SparkContext}
import stock.{Stock, StockUtil}
import util.HdfsFileUtil
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, HashMap}

/**
  * Created by yangshuai on 2016/1/16.
  */
object Scheduler {

  var prePriceMap = new mutable.HashMap[String, Float]()
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
//        val arr = sc.textFile("/smartuser/hbasedata/codes").filter(StockUtil.validCode).collect
        val arr = TableHbase.getStockCodesFromHbase(sc, 1).toArray
        stockList.clear
        SinaRequest.requestStockList(arr, afterRequest)

      }

      while (calculating) {

        try {
          //    if (taskIndex % 2 == 1) {
          prePriceMap = HdfsFileUtil.readTodayStockCodeByHour(9)
          SULogger.warn("pre size: " + prePriceMap.size)
          val currentPrice = HdfsFileUtil.readTodayStockCodeByHour(16)
          SULogger.warn("current size: " + currentPrice.size)
          val rdd = sc.parallelize(currentPrice.toSeq).map(x => getRateOfReturn(x._1, x._2)).filter(_.length > 0).collect.foreach(println)
          //    } else if (taskIndex == 3) {
        } catch {
          case e: Exception =>
            e.printStackTrace()
            SULogger.exception(e)
        }
        //
        //    }

        calculating = false
      }
    }
  }

  def afterRequest(): Unit = {
    calculating = true
  }

  def getRateOfReturn(code: String, price: Float): String = {
    val pre = prePriceMap.get(code)
    if (pre.isEmpty || pre.get == 0) {
      ""
    } else {
      val rate = RateOfReturnStrategy(StrategyConfig.STRATEGY_ONE).calculate(pre.get, price)
      code + "\t" + rate.toString
    }
  }

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
