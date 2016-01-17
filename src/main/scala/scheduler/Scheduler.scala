package scheduler

import java.io.{BufferedWriter, FileWriter, File}
import java.text.SimpleDateFormat
import java.util.Calendar

import dispatch._,Defaults._
import log.SULogger
import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkConf, SparkContext}
import util.HdfsFileUtil

import scala.util.{Failure, Success}

/**
  * Created by yangshuai on 2016/1/16.
  */
object Scheduler extends App {

  val MAX_CODE_NUMBER = 800.0

  def writeSth(str: String): Unit = {
    val today = Calendar.getInstance.getTime
    val format = new SimpleDateFormat("HH-mm-ss")
    val timeStr = format.format(today)

    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser")
    val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+"test")
    HdfsFileUtil.mkFile(currentPath + timeStr)
    HdfsFileUtil.writeString(currentPath + timeStr, str)
  }

  def validCode(code: String): Boolean = {
    if (code.length == 0)
      return false
    val head = code.charAt(0)
    if (head == '0' || head == '3' || head == '6' || head == '9')
      return true
    false
  }

//  SinaRequest.sendRequest(mutable.HashMap("list" -> "sh601006"))

  val conf =  new SparkConf().setMaster("local").setAppName("su").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer").set("spark.kryoserializer.buffer.max", "2000")
  val sc = new SparkContext(conf)
//  val stockList = TableHbase.getStockCodesFromHbase(sc, args(0).toInt)
//  val arr = sc.makeRDD(stockList).filter(validCode).sortBy(_.toInt).collect
  val arr = sc.textFile("/smartuser/hbasedata/codes").filter(validCode).collect





  var finalUrl = "http://hq.sinajs.cn/list="
  var i = 0
  val path = "/home/ys/code/smartuser/result"
  FileUtils.deleteQuietly(new File(path))
  val file = new File(path)
  file.createNewFile
  val fileWriter = new FileWriter(path, true)
  val bufferWriter = new BufferedWriter(fileWriter)

  var requestNum = Math.ceil(arr.size / MAX_CODE_NUMBER)

  while (i < arr.length) {
    val head = arr(i).charAt(0)
    if (head == '0' || head == '3') {
      finalUrl += "sz" + arr(i) + ","
    } else if (head == '6' || head == '9') {
      finalUrl += "sh" + arr(i) + ","
    }

    if ((i > 0 && i % MAX_CODE_NUMBER == 0) || i == arr.length - 1) {
      SULogger.warn("Send Request")
      println(finalUrl)
      send(finalUrl)
      finalUrl = "http://hq.sinajs.cn/list="
    }
    i += 1
  }

  sc.stop

  def send(finalUrl: String): Unit = {

    val req = url(finalUrl)
    val response = Http(req OK as.String)

    response onComplete {
      case Success(content) =>
        bufferWriter.write(content)
        requestNum -= 1
        if (requestNum == 0) {
          bufferWriter.close()
          Http.shutdown
        }

      case Failure(t) =>
        SULogger.warn("An error has occurred: " + t.getMessage)
    }
  }

//  val lines = sc.wholeTextFiles("hdfs://server:9000/smartuser/hbasedata/2016-01-16_21/")
//  lines.values.flatMap(_.split("\n")).map((_, 1)).reduceByKey(_+_).sortByKey(ascending = true).keys.filter(validCode).saveAsTextFile("hdfs://server:9000/smartuser/hbasedata/stockCodes")
}
