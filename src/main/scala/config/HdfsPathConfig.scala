package config

import util.TimeUtil

/**
  * Created by C.J.YOU on 2016/1/18.
  */
object HdfsPathConfig {

  val ROOT_DIR = "smartuser"
  val STOCK_SAVE_DIR = "Stock"
  val RETURN_DIR = "Return"
  val HBASE_DATA_SAVE_DIR = "hbasedata"
  val ALL_STOCKCODE_DIR = "stockCodes"
  val Date = TimeUtil.getDay(System.currentTimeMillis().toString)
}
