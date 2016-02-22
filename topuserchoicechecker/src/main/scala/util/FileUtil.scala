package util

import java.io.{PrintWriter, File, FileReader, BufferedReader}

import _root_.log.TUCLogger
import config.FileConfig
import stock.Stock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/27.
  */
object FileUtil {

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

  def readTopUserList(offset: Int): ListBuffer[String] = {

    val date = TimeUtil.getPreWorkDay(offset)
    val path = FileConfig.RANK_USER + "/" + date + "/15-9"

    readFile(path).map(_.split("\t")(0))
  }

  /**
    * 以Map形式取用户自选股信息
    *
    * @author yangshuai
    */
  def readUserInfoByDayAndHour(offset: Int, hour: Int): mutable.Map[String, Set[String]] = {

    val dateStr = TimeUtil.getPreWorkDay(offset)
    val destPath = FileConfig.USER_INFO + "/" + dateStr + "/" + hour

    TUCLogger.warn("Get user info from: " + destPath)

    val map = mutable.Map[String, Set[String]]()
    new File(destPath).listFiles().foreach(file => {
      map.put(file.getName, readFile(file.getAbsolutePath).toSet)
    })
    map
  }

  /**
    * 读取当天股票的大盘信息
    * @author C.J.YOU
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

  /** 创建目录 */
  def mkDir(name: String): Boolean = {

    val dir = new File(name)
    dir.mkdir
  }

  def createFile(path: String, lines: Seq[String]): Unit = {

    val writer = new PrintWriter(path, "UTF-8")

    for (line <- lines) {
      writer.println(line)
    }
    writer.close()
  }

  def createFile(path: String, line: String): Unit = {
    val writer = new PrintWriter(path, "UTF-8")
    writer.println(line)
    writer.close()
  }

  /**
    * 保存当天股票的涨幅值
    * @param arr
    * @param offset
    * @author C.J.YOU
    */
  def saveUserChoiceChecker(arr: Array[String],offset:Int): Unit ={
    val dateStr = TimeUtil.getPreWorkDay(offset)
    val destPath = FileConfig.USER_CHOICE_CHECKER + "/" + dateStr + "/" + "15"

    mkDir(FileConfig.USER_CHOICE_CHECKER + "/" + dateStr)

    createFile(destPath, arr)
  }

  /**
    * 读取当天的choicechecker
    * @param offset
    * @return
    */
  def readChoiceCheckerByDay(offset:Int,hour:Int): ListBuffer[String] ={

    var day = TimeUtil.getDay

    if (offset != 0) {
      day = TimeUtil.getPreWorkDay(offset)
    }

    val destPath = FileConfig.USER_CHOICE_CHECKER + "/" + day + "/" + hour.toString

    readFile(destPath)

  }
  // 用户检验的top
  def saveUserChoiceCheckerPercent(percent:String,offset:Int,top:Int): Unit ={
    val dateStr = TimeUtil.getPreWorkDay(offset)
    val destPath = FileConfig.USER_CHOICE_CHECKER_PERCENT + "/" + dateStr + "/" + top.toString

    mkDir(FileConfig.USER_CHOICE_CHECKER_PERCENT + "/" + dateStr)

    createFile(destPath, percent)
  }
}
