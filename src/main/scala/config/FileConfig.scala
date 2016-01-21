package config

import util.TimeUtil

/**
  * Created by C.J.YOU on 2016/1/18.
  */
object FileConfig {

  val ROOT_DIR = "/home/smartuser"
  val STOCK_INFO = ROOT_DIR + "/Stock"
  val RATE_OF_RETURN = ROOT_DIR + "/Return"
  val USER_INFO = ROOT_DIR + "/User"
  val Date = TimeUtil.getDay(System.currentTimeMillis().toString)
}
