package scheduler

import java.text.SimpleDateFormat
import java.util.Calendar

import util.HdfsFileUtil

/**
  * Created by yangshuai on 2016/1/16.
  */
object Scheduler extends App {

  /*HdfsFileUtil.setHdfsUri("hdfs://server:9000")
  HdfsFileUtil.setRootDir("smartuser")
  val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+"test")
  HdfsFileUtil.mkFile(currentPath+"file1")
  val stri = "one\ttwo\tthree"
  HdfsFileUtil.writeString(currentPath +"file1",stri)*/

  val today = Calendar.getInstance.getTime
  val format = new SimpleDateFormat("HH:mm:ss")
  val timeStr = format.format(today)
  println(timeStr)
}
