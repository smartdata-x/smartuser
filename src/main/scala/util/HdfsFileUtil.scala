package util

import java.io.{InputStreamReader, BufferedReader, ByteArrayOutputStream, ByteArrayInputStream}
import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.log4j.Logger

import scala.StringBuilder
import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/14.
  */
object HdfsFileUtil {
  private  var rootDir = new String
  private var hdfsUri = new String
  val logger = Logger.getRootLogger

  def getFileSystem:FileSystem ={
    val conf:Configuration = new  Configuration()
    conf.setBoolean("dfs.support.append", true)
    conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER")
    conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true")
    val fs = FileSystem.get(new URI(hdfsUri),conf,"root")
    fs
  }

  def setRootDir(string:String): Unit ={
    val fs = getFileSystem
    if(!fs.exists(new Path(getHdfsUri +"/"+ string))){
      fs.mkdirs(new Path(getHdfsUri +"/"+ string))
    }
    rootDir = getHdfsUri +"/"+ string + "/"
    fs.close()
  }

  def setHdfsUri(string:String): Unit ={
    hdfsUri = string
  }

  def getRootDir: String ={
    rootDir
  }

  def getHdfsUri: String ={
    hdfsUri
  }

  def mkDir(name:String): String ={
    val fs = getFileSystem
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
    val fs = getFileSystem
    if(!fs.exists(new Path(name))){
      fs.create(new Path(name))
      // System.out.println("mkfile sucess")
    }else{
      // System.out.println("file exist")
    }
    fs.close()
  }

  def writeString(fileName:String,str:String): Unit ={
    val fs = getFileSystem
    val split = str.split("\t")
    val strBuilder = new StringBuilder()
    try {
      println("fileName:" + fileName)
      val out = fs.append(new Path(fileName))
      if(split.length > 1){
        for(i <- 0 to split.length - 1){
          strBuilder.append(split(i) +"\t")
        }
        strBuilder.append("\n")
      } else{
        strBuilder.append(str +"\n")
      }
      if(strBuilder.nonEmpty){
        val in = new ByteArrayInputStream(strBuilder.toString.getBytes("UTF-8"))
        IOUtils.copyBytes(in, out, 4096, true)
        strBuilder.clear()
        in.close()
        out.close()
      }
    } catch {
      case e:Exception => println("writeString error")
        logger.error("[C.J.YOU]"+e.printStackTrace())
    } finally {
      fs.close()
    }
  }

  def writeStockCode(fileName:String,list: mutable.MutableList[String]): Unit ={
    val iterator: Iterator[String] =list.iterator
    val fs = getFileSystem
    val strBuilder = new StringBuilder()
    try {
      val out = fs.append(new Path(fileName))
      while (iterator.hasNext) {
        val field = iterator.next()
        // println("field:" + field)
        strBuilder.append(field +"\n")
      }
      if(strBuilder.nonEmpty){
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
  def readStockCode(path:String):mutable.MutableList[String]={
    val list = new mutable.MutableList[String]
    val fs = getFileSystem
    val in  = fs.open(new Path(path),4096)
    val bufferReader = new BufferedReader(new InputStreamReader(in))
    var line = bufferReader.readLine()
    while(line !=null){
      list.+=(line)
      line = bufferReader.readLine()
    }
    list
  }

  def saveStockCodes(fileName:String,list: mutable.MutableList[String]): Unit ={
    val listOfStockCodes = this.readStockCode(fileName)
    val fs = getFileSystem
    val strBuilder = new StringBuilder()
    try {
      val iterator: Iterator[String] =list.iterator
      val out = fs.append(new Path(fileName))
      if(listOfStockCodes != null){
        while (iterator.hasNext) {
          val field = iterator.next()
          if(!listOfStockCodes.contains(field)){
            strBuilder.append(field +"\n")
          }
        }
      }
      if(strBuilder.nonEmpty){
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
