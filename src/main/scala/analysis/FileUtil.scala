package analysis

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.log4j.Logger

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/14.
  */
class FileUtil {
  private  var rootDir = new String
  private var hdfsUri = new String

  def getFileSystem():FileSystem ={
    val conf:Configuration = new  Configuration()
    conf.setBoolean("dfs.support.append", true)
    val fs = FileSystem.get(new URI(hdfsUri),conf,"root")
    fs
  }
  def setRootDir(string:String): Unit ={
    val fs = getFileSystem()
    if(!fs.exists(new Path(getHdfsUri() +"/"+ string))){
      fs.mkdirs(new Path(getHdfsUri() +"/"+ string))
    }
    rootDir = getHdfsUri() +"/"+ string + "/"
    fs.close()
  }
  def setHdfsUri(string:String): Unit ={
    hdfsUri = string
  }
  def getRootDir(): String ={
    rootDir
  }
  def getHdfsUri(): String ={
    hdfsUri
  }
  def mkDir(name:String): String ={
    val fs = getFileSystem()
    if(!fs.exists(new Path(name))){
      fs.mkdirs(new Path(name))
      // System.out.println("mkDir sucess")
    }else{
      // System.out.println("Dir exist")
    }
    fs.close()
    name + "/"
  }
  def mkFile(name:String): Unit ={
    val fs = getFileSystem()
    if(!fs.exists(new Path(name))){
      fs.create(new Path(name))
      // System.out.println("mkfile sucess")
    }else{
      // System.out.println("file exist")
    }
    fs.close()
  }
  def writeToFile(fileName:String,list: mutable.MutableList[String],rowkey:String): Unit ={
    val iterator: Iterator[String] =list.iterator
    val fs = getFileSystem()
    val strBuilder = new StringBuilder()
    val logger = Logger.getRootLogger
    try {
      val out = fs.append(new Path(fileName))
      while (iterator.hasNext) {
        val field = iterator.next()
        // println("field:" + field)
        strBuilder.append(field +"\t"+rowkey+"\n")
      }
      if(!strBuilder.isEmpty){
        val in = new ByteArrayInputStream(strBuilder.toString.getBytes("UTF-8"))
        IOUtils.copyBytes(in, out, 4096, true)
        strBuilder.clear()
        in.close()
        out.close()
      }
    } catch {
      case e:Exception => println("write error")
        logger.error("[C.J.YOU]"+e.printStackTrace())
    } finally {
      fs.close()
    }
  }
}
object  TestFileUtil{
  def main(args: Array[String]): Unit ={
    val testFileUtil = new FileUtil
    testFileUtil.setHdfsUri("hdfs://server:9000")
    testFileUtil.setRootDir("smartuser")
    val currentPath = testFileUtil.mkDir("days")
    val list = new mutable.MutableList[String]
    list.+=("000001")
    list.+=("000002")
    testFileUtil.writeToFile(testFileUtil.getRootDir()+currentPath +"file1",list,"rowKey")
  }
}
