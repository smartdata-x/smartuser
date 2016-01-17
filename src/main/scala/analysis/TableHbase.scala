package analysis

import java.util.regex.Pattern

import log.SULogger
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.log4j.Logger
import org.apache.spark.SparkContext
import util.{HdfsFileUtil, TimeUtil}

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/15.
  * Hbase 操作子类，用来操作Hbase中的表1
  */
object TableHbase{

  val SINA_TABLE = "1"
  val SINA_COLUMN_FAMILY = "basic"
  val SINA_COLUMN_MEMBER = "content"
  val sinaTable = new HBase
  sinaTable.tableName = SINA_TABLE
  sinaTable.columnFamliy = SINA_COLUMN_FAMILY
  sinaTable.column = SINA_COLUMN_MEMBER

  /** 按照rowKey 将获取hbase的数据 */
  def get(rowKey:String,table:String,columnFamliy:String,column:String):Result = {
    val connection = ConnectionFactory.createConnection(HBaseConfiguration.create())
    val htable = connection.getTable(TableName.valueOf("1"))
    val get = new Get(Bytes.toBytes(rowKey))
    val resultOfGet = htable.get(get)
    resultOfGet
  }

  /**获取股票代码 */
  def getStockCodes(sc:String):mutable.MutableList[String] ={
    val followStockCodeList = new mutable.MutableList[String]
    val pattern = Pattern.compile("\"\\d{6}\"")
    val m = pattern.matcher(sc)
    if(m != null){
      while (m.find()) {
        val n = m.groupCount()
        for (i <- 0 to n ) {
          val outputValue = m.group(i)
          if (outputValue != null) {
            followStockCodeList.+=(outputValue.substring(1,7))
          }
        }
      }
    }
    followStockCodeList
  }

  /** 获取Userid */
  def getUserId(sc:String):String = {
    var userId = new String
    val pattern = Pattern.compile("\\[\'userid\'\\]\\s*=\\s*\'\\d{1,}\'")
    val m = pattern.matcher(sc)
    if(m != null){
      if(m.find()) {
        val outputValue = m.group(0)
        if (outputValue != null) {
          val patternId = Pattern.compile("\\d{1,}")
          val mId = patternId.matcher(outputValue)
          if(mId.find()) {
            val outputValueId = mId.group(0)
            userId = outputValueId
          }
        }
      }
    }
    userId
  }

  /** 合并List集合：主要要用于去重股票代码 */
  def mergeList(global_list: mutable.MutableList[String],temp_list:mutable.MutableList[String]): mutable.MutableList[String] ={
    if(temp_list != null) {
      val iterator = temp_list.iterator
      while (iterator.hasNext) {
        val value = iterator.next()
        if (!global_list.contains(value)) {
          global_list.+=(value)
        }
      }
    }
    global_list
  }

  /** 使用spark运行获取Hbase股票信息 */
  def getStockCodesFromHbase(sc:SparkContext, timeRange:Int): mutable.MutableList[String] = {
    SULogger.warn("enter <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")

    /** get hbase data */
    val scan = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - timeRange * 60 * 60 * 1000,currentTimeStamp)
    val conf = sinaTable.getConfigure(sinaTable.tableName,sinaTable.columnFamliy,sinaTable.column)
    sinaTable.setScan(scan)
    SULogger.warn(sinaTable.tableName)
    SULogger.warn(sinaTable.columnFamliy)
    SULogger.warn(sinaTable.column)

    val hbaseRdd = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])
    val collectResult = hbaseRdd.collect()
    SULogger.warn("before hdfsutil")

    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser/hbasedata")
    var g_day = new String
    var stockCodes = new mutable.MutableList[String]
    SULogger.warn("before foreach")
    collectResult.foreach(x => {
      try {
        SULogger.warn("enter foreach")
        val result = x._2
        val value = Bytes.toString(result.getValue(Bytes.toBytes(sinaTable.columnFamliy), Bytes.toBytes(sinaTable.column)))
        val timeStamp = result.getColumnLatestCell(Bytes.toBytes(sinaTable.columnFamliy), Bytes.toBytes(sinaTable.column)).getTimestamp
        SULogger.warn(timeStamp.toString)
        val days = TimeUtil.getDayAndHour(String.valueOf(timeStamp))
        g_day = days
        SULogger.warn(g_day)
        val followList = getStockCodes(value)
        val userId = getUserId(value)
        /** HDFS 操作*/
        val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+days)
        if(userId.trim.length > 0 && followList !=null){
          stockCodes = mergeList(stockCodes,followList)
          HdfsFileUtil.mkFile(currentPath+userId)
          // System.out.println("rowKey----"+rowKey)
          HdfsFileUtil.writeStockCode(currentPath + userId,followList)
        }
      } catch {
        case e:Exception => println("[C.J.YOU] writeToHdfsFile error")
          SULogger.error("[C.J.YOU]"+e.printStackTrace)
      }
    })
    /** 保存全局股票代码 */
    HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+""+"stockCodes")
    HdfsFileUtil.mkFile(HdfsFileUtil.getRootDir +"stockCodes"+"/"+g_day)
    HdfsFileUtil.writeStockCode(HdfsFileUtil.getRootDir +"stockCodes"+"/"+g_day,stockCodes)
    stockCodes
  }

  /** 直接获取Hbase股票信息,不使用spark运行 */
  def getStockCodesFromHbaseNoSpark(timeRange:Int): mutable.MutableList[String] ={
    var stockCodes = new mutable.MutableList[String]
    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser/hbasedata")
    /** get hbase data */
    val conf = sinaTable.getConfigure(sinaTable.tableName,sinaTable.columnFamliy,sinaTable.column)
    val connection = ConnectionFactory.createConnection(conf)
    val htable = connection.getTable(TableName.valueOf(sinaTable.tableName))
    val scan = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - timeRange * 60 * 60 * 1000,currentTimeStamp)
    sinaTable.setScan(scan)
    val resultScanner = htable.getScanner(scan)
    val resultScannerIterator = resultScanner.iterator()
    var g_day = new String
    try{
      while(resultScannerIterator.hasNext){
        val result = resultScannerIterator.next()
        val value = Bytes.toString(result.getValue(Bytes.toBytes(sinaTable.columnFamliy), Bytes.toBytes(sinaTable.column)))
        val timeStamp = result.getColumnLatestCell(Bytes.toBytes(sinaTable.columnFamliy), Bytes.toBytes(sinaTable.column)).getTimestamp
        val days = TimeUtil.getDay(String.valueOf(timeStamp))
        g_day = days
        val followList =getStockCodes(value)
        val userId = getUserId(value)
        /** HDFS 操作*/
        var currentPath = HdfsFileUtil.getRootDir + days
        currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir + days)
        println("userId:"+userId+":")
        if(userId.trim.length > 0 && followList !=null){
          stockCodes = mergeList(stockCodes,followList)
          HdfsFileUtil.mkFile(currentPath+userId)
          println("path:"+currentPath + userId)
          HdfsFileUtil.writeStockCode(currentPath + userId,followList)
        }
      }
    } catch {
      case e:Exception => println("[C.J.YOU] writeToHdfsFile error")
        Logger.getRootLogger.error("[C.J.YOU]"+e.printStackTrace)
    }
    /** 保存全局股票代码 */
    HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+""+"stockCodes")
    HdfsFileUtil.mkFile(HdfsFileUtil.getRootDir +"stockCodes"+"/"+g_day)
    HdfsFileUtil.writeStockCode(HdfsFileUtil.getRootDir +"stockCodes"+"/"+g_day,stockCodes)
    stockCodes
  }

}
