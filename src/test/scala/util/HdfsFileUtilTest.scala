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
    val testFileUtil = new HdfsFileUtil
    testFileUtil.setHdfsUri("hdfs://server:9000")
    testFileUtil.setRootDir("smartuser")
    val currentPath = testFileUtil.mkDir("days")
    val list = new mutable.MutableList[String]
    list.+=("000001")
    list.+=("000002")
    testFileUtil.writeToFile(testFileUtil.getRootDir()+currentPath +"file1",list,"rowKey")


  }
}
