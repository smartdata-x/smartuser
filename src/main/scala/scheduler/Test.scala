package scheduler

import log.SULogger
import util.HdfsFileUtil

/**
  * Created by yangshuai on 2016/1/18.
  */
object Test {

  def main(args: Array[String]): Unit = {
    SULogger.warn("begin")
    val map = HdfsFileUtil.readTodayStockCodeByHour(9)
    SULogger.warn("map size: " + map.size)
  }

}
