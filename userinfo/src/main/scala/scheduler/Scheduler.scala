package scheduler

import java.util.Calendar

import config.SparkConfig
import data.{FileUtil, HbaseUtil}
import log.UILogger
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/26.
  * 主流程
  */
object Scheduler {

  var userMap = new mutable.HashMap[String, ListBuffer[String]]()

  val conf =  new SparkConf().setMaster("local").setAppName("USER INFO").set("spark.serializer", SparkConfig.SPARK_SERIALIZER).set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    if (args.length == 0) {

      while(true) {

        UILogger.warn("Task begin.")
        HbaseUtil.readUserInfo(sc)
        FileUtil.saveUserStockInfo()

        UILogger.warn("Task complete.")
        Timer.waitToNextTask()
      }

    } else {

      val arr = args(0).split("-")
      val year = arr(0).toInt
      val month = arr(1).toInt - 1
      val day = arr(2).toInt
      val hour = arr(3).toInt

      val calendar = Calendar.getInstance()
      calendar.set(year, month, day)
      calendar.set(Calendar.HOUR_OF_DAY, hour)

      val timeStamp = calendar.getTimeInMillis
      HbaseUtil.readUserInfo(sc, timeStamp)
      FileUtil.saveUserStockInfo(timeStamp, hour)

      sc.stop

    }

  }
}
