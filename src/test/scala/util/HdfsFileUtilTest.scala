package util

import analysis.DataAnalysis
import analysis.analysis.DataAnalysis
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/16.
  */
class HdfsFileUtilTest extends FlatSpec with Matchers {

  it should "" in {

  }
  "saveStockCodes and MergeList " should "work" in{
    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser")
    val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+"days")
    HdfsFileUtil.mkFile(currentPath+"file1")
    var list = new mutable.MutableList[String]
    println("currentPath:"+currentPath)
    list.+=("000001")
    list.+=("000002")
    val list_2 = new mutable.MutableList[String]
    list_2.+=("000001")
    list_2.+=("000002")
    list_2.+=("000003")
    list_2.+=("000005")
    list = DataAnalysis.MergeList(list,list_2)
    // HdfsFileUtil.writeStockCode(currentPath +"file1",list)
    HdfsFileUtil.saveStockCodes(currentPath +"file1",list)
  }

  "read and write method" should "work" in {
    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser")
    val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+"days")
    HdfsFileUtil.mkFile(currentPath+"file1")
    val list = new mutable.MutableList[String]
    list.+=("000001")
    list.+=("000002")
    HdfsFileUtil.writeStockCode(currentPath +"file1",list)


  }
  "writeString method" should "work" in {
    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser")
    val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+"days")
    HdfsFileUtil.mkFile(currentPath+"file1")
    val stri = "one\ttwo\tthree"
    HdfsFileUtil.writeString(currentPath +"file1",stri)

  }
}
