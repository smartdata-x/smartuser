package util

import java.io.{File, FileReader, BufferedReader}

import config.FileConfig

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/26.
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

}
