package calculate

import util.HdfsFileUtil

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/15.
  */
class RateOfReturn {
    def getUserStock(date:String,id:String): mutable.MutableList[String] ={
      val fileUtil = new HdfsFileUtil
      fileUtil.setHdfsUri("hdfs://server:9000")
      fileUtil.setRootDir("smartuser")
      val stockList = fileUtil.readFileContent(fileUtil.getRootDir()+date+"/"+id)
      stockList
    }
}
