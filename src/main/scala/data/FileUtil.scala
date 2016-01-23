package data

import java.io._

import _root_.util.TimeUtil
import config.FileConfig
import log.SULogger
import org.apache.hadoop.fs.Path
import scheduler.Scheduler
import stock.Stock

import scala.collection.mutable
import scala.collection.mutable.{HashMap, ListBuffer}

/**
  * Created by C.J.YOU on 2016/1/14.
  * HDFS操作的工具类
  */
object FileUtil {

  /** 创建目录 */
  def mkDir(name: String): Boolean = {

    val dir = new File(name)
    dir.mkdir
  }

  /** 写入股票代码 */
  def writeStockCode(fileName: String, list: Array[String]): Unit = {

    val writer = new PrintWriter(fileName, "UTF-8")
    for (line <- list) {
      writer.write(line)
    }
    writer.close()
  }

  def readFile(path: String): ListBuffer[String] = {

    var lines = new ListBuffer[String]()

    val br = new BufferedReader(new FileReader(path))
    try {
      var line = br.readLine()

      while (line != null) {
        lines += line
        line = br.readLine()
      }
      lines
    } finally {
      br.close()
    }
  }

  /**
    * override the old one
    */
  def createFile(path: String, lines: Seq[String]): Unit = {
    val writer = new PrintWriter(path, "UTF-8")
    for (line <- lines) {
      val validStockCode = Stock.getTypeOfStockCode(line)
      writer.println(validStockCode)
    }
    writer.close()
  }

  /**
    * 读取当天给定的的小时数的股票代码和价格
    * @author yangshuai
    */
  def readTodayStockCodeByHour(hour: Int): mutable.HashMap[String, Stock] = {

    val destPath = FileConfig.STOCK_INFO + "/" + TimeUtil.getDay(System.currentTimeMillis().toString) + "/" + hour.toString

    val list = readFile(destPath)

    val map = mutable.HashMap[String, Stock]()

    for (item <- list) {
      val stock = Stock(item)
      map.put(stock.code, stock)
    }

    map
  }

  /** 写入股票对象,包括股票所有信息 */
  def writeStockList(list: ListBuffer[Stock]): Unit = {

    /** 创建对应的目录 */
    val fileDayDir = TimeUtil.getDay(System.currentTimeMillis().toString)
    val fileName = TimeUtil.getCurrentHour()
    val destPath = FileConfig.STOCK_INFO + "/" + fileDayDir + "/" + fileName

    val strList = list.map(x => x.toString())
    SULogger.warn("Save " + strList.size + " stocks info to: " + destPath)
    mkDir(FileConfig.STOCK_INFO + "/" + fileDayDir)
    createFile(destPath, strList)
  }

  /** 写回报率文件方法  **/
  def writeRateOfReturnStrategyOneFile(list: Array[String],start: Int,end:Int): Unit = {

    /** 创建对应的目录 */
    val fileDayDir =TimeUtil.getDay(System.currentTimeMillis().toString)
    val fileName =start + "-" + end
    val destPath = FileConfig.RATE_OF_RETURN + "/" + fileDayDir + "/" + fileName
    SULogger.warn(destPath)
    mkDir(FileConfig.RATE_OF_RETURN + "/" + fileDayDir)
    createFile(destPath, list)
  }

  /**
    * 保存用户关注的股票信息
 *
    * @author yangshuai
    */
  def saveUserStockInfo(): Unit = {

    /** 创建对应的目录 */
    val fileDayDir = TimeUtil.getDay(System.currentTimeMillis().toString)
    val fileName = TimeUtil.getCurrentHour()
    val destPath = FileConfig.USER_INFO + "/" + fileDayDir + "/" + fileName

    SULogger.warn("Save user info to " + destPath)
    mkDir(FileConfig.USER_INFO + "/" + fileDayDir)
    mkDir(destPath)

    for (item <- Scheduler.userMap) {
      /** HDFS 操作*/
      val userId = item._1
      val stockCodeList = item._2
      if(userId.trim.length > 0 && stockCodeList != null){
        createFile(destPath + "/" + userId, stockCodeList)
      }
    }
  }

  /**
    *获取用户关注股票信息
    */
  def getUserStockInfo(dstpath:String): HashMap[String,ListBuffer[String]] ={
    var hashMap = new HashMap[String,ListBuffer[String]]
    val dir = new File(dstpath)
    val fileList = readUserFile(dir)
    fileList.foreach(result =>{
      val filePath = result.getAbsolutePath
      val fileName = result.getName
      val stockList = readFile(filePath)
      hashMap.+=((fileName,stockList))
    })
    hashMap
  }
  def readUserFile(dir: File): Iterator[File] = {
    val d = dir.listFiles.filter(_.isDirectory)
    val f = dir.listFiles.toIterator
    f ++ d.toIterator.flatMap(readUserFile _)
  }
}
