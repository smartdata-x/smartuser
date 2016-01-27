package util

import java.io.{File, FileReader, BufferedReader}

import _root_.log.TUCLogger
import config.FileConfig

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

  def readTopUserList: ListBuffer[String] = {

    val date = TimeUtil.getPreWorkDay(-1)
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

}
