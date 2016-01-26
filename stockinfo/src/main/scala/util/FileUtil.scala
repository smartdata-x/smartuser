package util

import java.io.{PrintWriter, File, FileReader, BufferedReader}

import config.FileConfig
import log.SILogger
import stock.Stock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/26.
  */
object FileUtil {

  /**
    * override the old one
    */
  def createFile(path: String, lines: Seq[String]): Unit = {

    val writer = new PrintWriter(path, "UTF-8")

    for (line <- lines) {
      writer.println(line)
    }
    writer.close()
  }

  def mkDir(name: String): Boolean = {

    val dir = new File(name)
    dir.mkdir
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

  def readLatestStocks(): mutable.Set[String] = {

    val set = mutable.Set[String]()

    val path = FileConfig.USER_INFO + "/" + TimeUtil.getDay
    val hour = TimeUtil.getCurrentHour
    var dir = new File(path + "/" + hour)

    if (!dir.exists()) {
      dir = new File(path + "/" + (hour + 1))
    }

    dir.listFiles().foreach(file => {
      val filePath = file.getAbsolutePath
      val fileName = file.getName
      if (fileName.matches("\\d+")) {
        val stockList = readFile(filePath)
        for (stock <- stockList) {
          set.add(stock)
        }
      }
    })

    set
  }

  /** 写入股票对象,包括股票所有信息 */
  def writeStockList(list: ListBuffer[Stock]): Unit = {

    /** 创建对应的目录 */
    val fileDayDir = TimeUtil.getDay
    val fileName = TimeUtil.getCurrentHour
    val destPath = FileConfig.STOCK_INFO + "/" + fileDayDir + "/" + fileName

    val strList = list.map(x => x.toString())
    SILogger.warn("Save " + strList.size + " stocks info to: " + destPath)
    mkDir(FileConfig.STOCK_INFO + "/" + fileDayDir)
    createFile(destPath, strList)
  }
}
