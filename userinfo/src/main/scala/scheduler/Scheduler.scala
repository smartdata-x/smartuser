package scheduler

import java.util.Calendar
import java.util.concurrent.TimeUnit

import config.SparkConfig
import data.{FileUtil, HbaseUtil}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/26.
  * 主流程
  */
object Scheduler {

  var userMap = new mutable.HashMap[String, ListBuffer[String]]()

  val conf =  new SparkConf().setMaster("local").setAppName("su").set("spark.serializer", SparkConfig.SPARK_SERIALIZER).set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    while(true) {

      HbaseUtil.readUserInfo(sc)
      FileUtil.saveUserStockInfo()

      Timer.waitToNextTask
    }
  }

}
