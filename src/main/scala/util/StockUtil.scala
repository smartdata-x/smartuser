package util

import scala.collection.mutable

/**
  * Created by yangshuai on 2016/1/16.
  */
object StockUtil {
  def getUserStock(date:String,id:String): mutable.MutableList[String] ={
    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser")
    val stockList = HdfsFileUtil.readFileContent(HdfsFileUtil.getRootDir + date + "/" + id)
    stockList
  }
}
