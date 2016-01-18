package util

import java.io.{BufferedReader, ByteArrayInputStream, InputStreamReader}
import java.net.URI

import config.HbaseConfig
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.log4j.Logger
import stock.Stock

import scala.collection.mutable
import scala.collection.mutable.HashMap

/**
  * Created by C.J.YOU on 2016/1/14.
  * HDFS操作的工具类
  */
object HdfsFileUtil {
  private var rootDir = new String
  private var hdfsUri = new String
  val logger = Logger.getRootLogger

  /** 获取能操作hdfs的对象 */
  def getFileSystem: FileSystem = {
    val conf: Configuration = new Configuration()
    conf.setBoolean("dfs.support.append", true)
    conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER")
    conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true")
    val fs = FileSystem.get(new URI(hdfsUri), conf, "root")
    fs
  }

  /** 设置操作的根目录 */
  def setRootDir(string: String): Unit = {
    val fs = getFileSystem
    if (!fs.exists(new Path(getHdfsUri + "/" + string))) {
      fs.mkdirs(new Path(getHdfsUri + "/" + string))
    }
    rootDir = getHdfsUri + "/" + string + "/"
    fs.close()
  }

  /** 设置hdfs的访问地址 */
  def setHdfsUri(string: String): Unit = {
    hdfsUri = string
  }

  /** 获取根目录 */
  def getRootDir: String = {
    rootDir
  }

  /** 获取hdfs的访问地址 */
  def getHdfsUri: String = {
    hdfsUri
  }

  /** 创建目录 */
  def mkDir(name: String): String = {
    val fs = getFileSystem
    if (!fs.exists(new Path(name))) {
      fs.mkdirs(new Path(name))
      // System.out.println("mkDir sucess")
    } else {
      // System.out.println("Dir exist")
    }
    fs.close()
    name + "/"
  }

  /** 创建文件 */
  def mkFile(name: String): Unit = {
    val fs = getFileSystem
    if (!fs.exists(new Path(name))) {
      fs.create(new Path(name))
      // System.out.println("mkfile sucess")
    } else {
      // System.out.println("file exist")
    }
    fs.close()
  }

  /** 写入按tab分割的字符数据 */
  def writeString(fileName: String, str: String): Unit = {
    val fs = getFileSystem
    val split = str.split("\t")
    val strBuilder = new StringBuilder()
    try {
      println("fileName:" + fileName)
      val out = fs.append(new Path(fileName))
      if (split.length > 1) {
        for (i <- 0 to split.length - 1) {
          strBuilder.append(split(i) + "\t")
        }
        strBuilder.append("\n")
      } else {
        strBuilder.append(str + "\n")
      }
      if (strBuilder.nonEmpty) {
        val in = new ByteArrayInputStream(strBuilder.toString.getBytes("UTF-8"))
        IOUtils.copyBytes(in, out, 4096, true)
        strBuilder.clear()
        in.close()
        out.close()
      }
    } catch {
      case e: Exception => println("writeString error")
        logger.error("[C.J.YOU]" + e.printStackTrace())
    } finally {
      fs.close()
    }
  }

  /** 写入股票代码 */
  def writeStockCode(fileName: String, list: mutable.MutableList[String]): Unit = {
    val iterator: Iterator[String] = list.iterator
    val fs = getFileSystem
    val strBuilder = new StringBuilder()
    try {
      val out = fs.append(new Path(fileName))
      while (iterator.hasNext) {
        val field = iterator.next()
        // println("field:" + field)
        strBuilder.append(field + "\n")
      }
      if (strBuilder.nonEmpty) {
        val in = new ByteArrayInputStream(strBuilder.toString.getBytes("UTF-8"))
        IOUtils.copyBytes(in, out, 4096, true)
        strBuilder.clear()
        in.close()
        out.close()
      }
    } catch {
      case e: Exception => println("write error")
        logger.error("[C.J.YOU]" + e.printStackTrace())
    } finally {
      fs.close()
    }
  }

  /** 读取股票代码 */
  def readStockCode(path: String): mutable.MutableList[String] = {
    val list = new mutable.MutableList[String]
    val fs = getFileSystem
    val in = fs.open(new Path(path), 4096)
    val bufferReader = new BufferedReader(new InputStreamReader(in))
    var line = bufferReader.readLine()
    while (line != null) {
      list.+=(line)
      line = bufferReader.readLine()
    }
    list
  }

  /** 遍历目录，获取对应目录下文件与文件内容的Map集合  */
  def getDirectoryContentFromHdfs(dstpath: String): HashMap[String, mutable.MutableList[String]] = {
    var hashMap = new HashMap[String, mutable.MutableList[String]]
    val fs = getFileSystem
    if (fs.isDirectory(new Path(dstpath))) {
      val fileList = fs.listStatus(new Path(dstpath))
      for (i <- 0 to fileList.length - 1) {
        val filePath = fileList(i).getPath.toString
        val keyOfFileName = fileList(i).getPath.getName
        hashMap.+=(keyOfFileName.->(readStockCode(filePath)))
      }
    }
    fs.close()
    hashMap
  }

  /** 股票代码去重保存到文件  */
  def saveStockCodes(fileName: String, list: mutable.MutableList[String]): Unit = {
    val listOfStockCodes = this.readStockCode(fileName)
    val fs = getFileSystem
    val strBuilder = new StringBuilder()
    try {
      val iterator: Iterator[String] = list.iterator
      val out = fs.append(new Path(fileName))
      if (listOfStockCodes != null) {
        while (iterator.hasNext) {
          val field = iterator.next()
          if (!listOfStockCodes.contains(field)) {
            strBuilder.append(field + "\n")
          }
        }
      }
      if (strBuilder.nonEmpty) {
        val in = new ByteArrayInputStream(strBuilder.toString.getBytes("UTF-8"))
        IOUtils.copyBytes(in, out, 4096, true)
        strBuilder.clear()
        in.close()
        out.close()
      }
    } catch {
      case e: Exception => println("write error")
        logger.error("[C.J.YOU]" + e.printStackTrace())
    } finally {
      fs.close()
    }
  }

  /** 写入股票对象,包括股票代码和当前价格 */
  def writeStockObject(list: mutable.MutableList[Stock]): Unit = {
    /** 创建对应的目录 */
    HdfsFileUtil.setHdfsUri(HbaseConfig.HBASE_URL)
    HdfsFileUtil.setRootDir("smartuser/strategyone")
    val fileDayDir = TimeUtil.getDay(System.currentTimeMillis().toString)
    val currentDir = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir + fileDayDir)
    val fileName = TimeUtil.getDayAndHour(System.currentTimeMillis().toString).split("_")(1)
    val destPath = currentDir + fileName
    HdfsFileUtil.mkFile(destPath)
    /*　写数据到HDFS操作  */
    val iterator: Iterator[Stock] = list.iterator
    val fs = getFileSystem
    val strBuilder = new StringBuilder()
    try {
      val out = fs.append(new Path(destPath))
      while (iterator.hasNext) {
        val field = iterator.next()
        /** 待完善 根据股票名称获取股票代码的函数
          * ？？？？
          * */
        strBuilder.append(field.name + "\t"+field.currentPrice+"\n")
      }
      if (strBuilder.nonEmpty) {
        val in = new ByteArrayInputStream(strBuilder.toString.getBytes("UTF-8"))
        IOUtils.copyBytes(in, out, 4096, true)
        strBuilder.clear()
        in.close()
        out.close()
      }
    }catch {
      case e: Exception => println("writeStockObject error")
        logger.error("[C.J.YOU]" + e.printStackTrace())
    } finally {
      fs.close()
    }
  }
}
