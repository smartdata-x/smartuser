package util

import config.{HdfsPathConfig, HbaseConfig}

import scala.collection.mutable

/**
  * Created by yangshuai on 2016/1/16.
  */
object StockUtil {
  def getUserStock(date:String,id:String): mutable.MutableList[String] ={
    HdfsFileUtil.setHdfsUri(HbaseConfig.HBASE_URL)
    HdfsFileUtil.setRootDir(HdfsPathConfig.ROOT_DIR)
    val stockList = HdfsFileUtil.readStockCode(HdfsFileUtil.getRootDir + date + "/" + id)
    stockList
  }
}
