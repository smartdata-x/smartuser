package data

import java.io._

import config.FileConfig
import log.UILogger
import scheduler.Scheduler
import util.TimeUtil

/**
  * Created by yangshuai on 2016/1/26.
  */
object FileUtil {

  def mkDir(name: String): Boolean = {

    val dir = new File(name)
    dir.mkdir
  }

  /**
    * override the old one
    *
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
    * 保存用户关注的股票信息
    *
    * @author yangshuai
    */
  def saveUserStockInfo(): Unit = {

    val fileDayDir = TimeUtil.getDay(System.currentTimeMillis().toString)
    val fileName = TimeUtil.getCurrentHour
    val destPath = FileConfig.USER_INFO + "/" + fileDayDir + "/" + fileName

    UILogger.warn("Save user info to " + destPath)
    mkDir(FileConfig.USER_INFO + "/" + fileDayDir)
    mkDir(destPath)

    for (item <- Scheduler.userMap) {

      val userId = item._1
      val stockCodeList = item._2
      if(userId.trim.length > 0 && stockCodeList != null){
        createFile(destPath + "/" + userId, stockCodeList)
      }
    }
  }
}
