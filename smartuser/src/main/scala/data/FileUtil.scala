package data

import java.io._

import _root_.util.TimeUtil
import config.FileConfig
import log.SULogger
import stock.Stock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

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
    * @author yangshuai
    */
  def createFile(path: String, lines: Seq[String]): Unit = {

    val writer = new PrintWriter(path, "UTF-8")

    for (line <- lines) {
      writer.println(line)
    }
    writer.close()
  }

  /**
    * override the old one
    * @author yangshuai
    */
  def createFile(path: String, lines: Seq[String], num: Int): Unit = {

    val writer = new PrintWriter(path, "UTF-8")
    var count = num

    for (line <- lines) {
      if (count > 0) {
        writer.println(line)
        count -= 1
      }
    }

    writer.close()
  }

  /**
    * 读取当天给定的的小时数的股票代码和价格
    * @author yangshuai
    */
  def readStockCodeByDayAndHour(offset: Int, hour: Int): mutable.HashMap[String, Stock] = {

    var day = TimeUtil.getDay

    if (offset != 0) {
      day = TimeUtil.getPreWorkDay(offset)
    }

    val destPath = FileConfig.STOCK_INFO + "/" + day + "/" + hour.toString

    val list = readFile(destPath)

    val map = mutable.HashMap[String, Stock]()

    for (item <- list) {
      val stock = Stock(item)
      map.put(stock.code, stock)
    }

    map
  }

  /**
    * 获得前一个工作日的股票Map
    * @author yangshuai
    */
  def readPreWorkDayStockCodes: mutable.HashMap[String, Stock] = {

    val dateStr = TimeUtil.getPreWorkDay(-1)
    val destPath = FileConfig.STOCK_INFO + "/" + dateStr + "/" + 9.toString

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
    val fileDayDir = TimeUtil.getDay
    val fileName = TimeUtil.getCurrentHour
    val destPath = FileConfig.STOCK_INFO + "/" + fileDayDir + "/" + fileName

    val strList = list.map(x => x.toString())
    SULogger.warn("Save " + strList.size + " stocks info to: " + destPath)
    mkDir(FileConfig.STOCK_INFO + "/" + fileDayDir)
    createFile(destPath, strList)
  }

  /** 写回报率文件方法  **/
  def writeRateOfReturnStrategyOneFile(list: Array[String],start: String,end:String): Unit = {

    /** 创建对应的目录 */
    val fileDayDir =TimeUtil.getDay
    val fileName =start + "--" + end
    val destPath = FileConfig.RATE_OF_RETURN_STOCK + "/" + fileDayDir + "/" + fileName
    SULogger.warn("Write rate of return to: " + destPath)
    mkDir(FileConfig.RATE_OF_RETURN_STOCK + "/" + fileDayDir)
    createFile(destPath, list)
  }

  def saveUserReturnInfo(arr: Array[String], fileName: String): Unit = {

    /** 创建对应的目录 */
    val fileDayDir = TimeUtil.getDay
    val destPath = FileConfig.RATE_OF_RETURN_USER + "/" + fileDayDir + "/" + fileName

    mkDir(FileConfig.RATE_OF_RETURN_USER + "/" + fileDayDir)
    createFile(destPath, arr)
  }

  /**
    *获取用户关注股票信息
    */
  def getUserStockInfo(dstpath:String): mutable.HashMap[String,ListBuffer[String]] ={

    var hashMap = new mutable.HashMap[String,ListBuffer[String]]
    val dir = new File(dstpath)
    val fileList = readUserFile(dir)

    fileList.foreach(result =>{
      val filePath = result.getAbsolutePath
      val fileName = result.getName
      if (fileName.matches("\\d+")) {
        val stockList = readFile(filePath)
        hashMap.+=((fileName,stockList))
      }
    })

    hashMap
  }

  def readUserFile(dir: File): Iterator[File] = {
    val d = dir.listFiles.filter(_.isDirectory)
    val f = dir.listFiles.toIterator
    f ++ d.toIterator.flatMap(readUserFile)
  }

  /**
    * 保存股票被关注人次信息
    * @author yangshuai
    */
  def saveStockRankInfo(infos: Seq[String], fileName: String): Unit = {
    /** 创建对应的目录 */
    val fileDayDir = TimeUtil.getDay
    val destPath = FileConfig.RANK_USER + "/" + fileDayDir + "/" + fileName

    mkDir(FileConfig.RANK_USER + "/" + fileDayDir)
    createFile(destPath, infos)
  }

  /**
    * 以Map的形式获取文件内容
    * @author yangshuai
    */
  def getMapFromFile(path: String): mutable.Map[String, String] = {

    val list = readFile(path)
    val map = mutable.Map[String, String]()
    for (line <- list) {
      val arr = line.split("\t")
      map.put(arr(0), arr(1))
    }

    map
  }

  /**
    * 以Map形式取用户自选股信息
    * @author yangshuai
    */
  def readUserInfoByDayAndHour(offset: Int, hour: Int): mutable.Map[String, Set[String]] = {

    val dateStr = TimeUtil.getPreWorkDay(offset)
    val destPath = FileConfig.USER_INFO + "/" + dateStr + "/" + hour

    SULogger.warn("Get user info from: " + destPath)

    val map = mutable.Map[String, Set[String]]()
    new File(destPath).listFiles().foreach(file => {
      map.put(file.getName, readFile(file.getAbsolutePath).toSet)
    })

    map
  }

  /**
    * 保存用户回报率排名信息
    * @author yangshuai
    */
  def saveUserRank(arr: Array[String], num: Int): Unit = {

    val dateStr = TimeUtil.getDay
    val destPath = FileConfig.RANK_USER + "/" + dateStr + "/" + "15-9"

    mkDir(FileConfig.RANK_USER + "/" + dateStr)

    createFile(destPath, arr, num)
  }


}
