package util

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/16.
  */
class HdfsFileUtilTest extends FlatSpec with Matchers {

  it should "" in {

  }

  "read and write method" should "work" in {
    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser")
    val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+"days")
    HdfsFileUtil.mkFile(currentPath+"file1")
    val list = new mutable.MutableList[String]
    list.+=("000001")
    list.+=("000002")
    HdfsFileUtil.writeToFile(currentPath +"file1",list)


  }
}
