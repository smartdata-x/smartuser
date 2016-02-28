package scheduler

import java.io.File
import java.util.Calendar
import javax.xml.parsers.DocumentBuilderFactory

import config.HbaseConfig
import data.{FileUtil, HbaseUtil}
import log.UILogger
import org.apache.spark.{SparkConf, SparkContext}
import org.w3c.dom.Element

/**
  * Created by yangshuai on 2016/1/26.
  * 主流程
  */
object Scheduler {

  val conf =  new SparkConf().setAppName("USER_INFO")
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    configure(args(0))

    if (args.length == 1) {

      while(true) {

        UILogger.warn("Task begin.")
        val userMap = HbaseUtil.readUserInfo(sc)
        FileUtil.saveUserStockInfo(userMap)

        UILogger.warn("Task complete.")
        Timer.waitToNextTask()
      }

    } else {

      val arr = args(1).split("-")
      val year = arr(0).toInt
      val month = arr(1).toInt - 1
      val day = arr(2).toInt
      val hour = arr(3).toInt

      val calendar = Calendar.getInstance()
      calendar.set(year, month, day)
      calendar.set(Calendar.HOUR_OF_DAY, hour)

      val timeStamp = calendar.getTimeInMillis
      val userMap = HbaseUtil.readUserInfo(sc, timeStamp)
      FileUtil.saveUserStockInfo(userMap, timeStamp, hour)

      sc.stop
    }

  }

  def configure(path: String): Unit = {

    val file = new File(path)

    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

    val hbaseRoot = doc.getElementsByTagName("hbase").item(0).asInstanceOf[Element]
    val dir = hbaseRoot.getElementsByTagName("dir").item(0).getTextContent
    val quorum = hbaseRoot.getElementsByTagName("quorum").item(0).getTextContent
    val port = hbaseRoot.getElementsByTagName("port").item(0).getTextContent

    HbaseConfig.init(dir, quorum, port)

  }

}
