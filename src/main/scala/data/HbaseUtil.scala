package data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern

import config.HDFSPathConfig
import log.SULogger
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.spark.SparkContext
import scheduler.Scheduler

import scala.collection.mutable.ListBuffer

/**
  * Created by C.J.YOU on 2016/1/15.
  * Hbase 操作子类，用来操作Hbase中的表1
  */
object HbaseUtil {

  val SINA_TABLE = "1"
  val SINA_COLUMN_FAMILY = "basic"
  val SINA_COLUMN_MEMBER = "content"

  val sinaTable = new HBase
  sinaTable.tableName = SINA_TABLE
  sinaTable.columnFamily = SINA_COLUMN_FAMILY
  sinaTable.column = SINA_COLUMN_MEMBER

  /**
    * 获取所有用户关注的股票代码(有重复)
    */
  def getAllStockCodes(response: String): ListBuffer[String] ={
    val followStockCodeList = ListBuffer[String]()
    val pattern = "(?<=\")\\d{6}(?=\")".r
    val iterator = pattern.findAllMatchIn(response)

    while(iterator.hasNext) {
      val item = iterator.next
      followStockCodeList.+=(item.toString)
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

  /** 使用spark运行获取Hbase股票信息 */
  def getStockCodes(sc:SparkContext, timeRange:Int): Array[String] = {

    /** get hbase data */
    val scan = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - timeRange * 60 * 60 * 1000,currentTimeStamp)
    val conf = sinaTable.getConfigure(sinaTable.tableName,sinaTable.columnFamily,sinaTable.column)
    sinaTable.setScan(scan)

    val users = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])
    SULogger.warn("total stock number: " + users.count.toString)

    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH")
    val g_day: String = sdf.format(new Date)


    val stockCodes = users.flatMap(x => {
      try {
        val result = x._2
        val value = Bytes.toString(result.getValue(Bytes.toBytes(sinaTable.columnFamily), Bytes.toBytes(sinaTable.column)))
        val userId = getUserId(value)
        val userStockList = getAllStockCodes(value)
        Scheduler.userMap.put(userId, userStockList)
        userStockList
      } catch {
        case e:Exception => println("[C.J.YOU] writeToHdfsFile error")
          SULogger.error("[C.J.YOU]"+e.printStackTrace)
          ListBuffer[String]()
      }
    }).distinct.collect

    /** 保存全局股票代码 */
    if(stockCodes.nonEmpty){

      SULogger.warn("stock codes not null")
      HDFSFileUtil.mkDir(HDFSPathConfig.STOCK_INFO)
      HDFSFileUtil.mkFile(HDFSPathConfig.STOCK_INFO +"/" + g_day)
      HDFSFileUtil.writeStockCode(HDFSPathConfig.STOCK_INFO + "/" + g_day, stockCodes)
    }

    SULogger.warn("distinct stock number: " + stockCodes.length.toString)

    stockCodes
  }

  /** 直接获取Hbase股票信息,不使用spark运行 */
  /*def getStockCodesFromHbaseNoSpark(timeRange:Int): mutable.MutableList[String] ={
    var stockCodes = new ListBuffer[String]()
    HdfsFileUtil.setHdfsUri(HbaseConfig.HBASE_URL)
    HdfsFileUtil.setRootDir(HdfsPathConfig.ROOT_DIR +"/"+HdfsPathConfig.HBASE_DATA_SAVE_DIR)
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
          HdfsFileUtil.writeStockCode(currentPath + userId,followList.toArray)
        }
      }
    } catch {
      case e:Exception => println("[C.J.YOU] writeToHdfsFile error")
        Logger.getRootLogger.error("[C.J.YOU]"+e.printStackTrace)
    }
    /** 保存全局股票代码 */
    HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir + HdfsPathConfig.ALL_STOCKCODE_DIR)
    HdfsFileUtil.mkFile(HdfsFileUtil.getRootDir + HdfsPathConfig.ALL_STOCKCODE_DIR+"/"+g_day)
    HdfsFileUtil.writeStockCode(HdfsFileUtil.getRootDir + HdfsPathConfig.ALL_STOCKCODE_DIR +"/"+g_day,stockCodes.toArray)
    stockCodes
  }*/

}
