package analysis

import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern

import config.{HdfsPathConfig, HbaseConfig}
import log.SULogger
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.log4j.Logger
import org.apache.spark.SparkContext
import scheduler.Scheduler
import util.{HdfsFileUtil, TimeUtil}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

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
  def getStockCodes(response: String): ListBuffer[String] ={
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
    var finalUserId = new String
    val pattern = Pattern.compile("\\[\'userid\'\\]\\s*=\\s*\'\\d{1,}\'")
    val userIdMatcher = pattern.matcher(sc)
    if(userIdMatcher != null){
      if(userIdMatcher.find()) {
        val outputValue = userIdMatcher.group(0)
        if (outputValue != null) {
          val patternId = Pattern.compile("\\d{1,}")
          val userId = patternId.matcher(outputValue)
          if(userId.find()) {
            val outputValueId = userId.group(0)
            finalUserId = outputValueId
          }
        }
      }
    }
    finalUserId
  }

  /** 使用spark运行获取Hbase股票信息 */
  def getStockCodesFromHbase(sc:SparkContext, timeRange:Int): Array[String] = {

    /** get hbase data */
    val scan = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - timeRange * 60 * 60 * 1000,currentTimeStamp)
    val conf = sinaTable.getConfigure(sinaTable.tableName,sinaTable.columnFamliy,sinaTable.column)

    SULogger.warn(sinaTable.tableName)
    SULogger.warn(sinaTable.columnFamliy)
    SULogger.warn(sinaTable.column)

    sinaTable.setScan(scan)

    val users = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])

    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH")
    val g_day: String = sdf.format(new Date)

    SULogger.warn("total stock number: " + users.count.toString)
    val stockCodes = users.flatMap(x => {
      try {
        val result = x._2
        val value = Bytes.toString(result.getValue(Bytes.toBytes(sinaTable.columnFamliy), Bytes.toBytes(sinaTable.column)))
        val userId = getUserId(value)
        val userStockList = getStockCodes(value)
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
      HdfsFileUtil.mkDir(HdfsPathConfig.STOCK_INFO)
      HdfsFileUtil.mkFile(HdfsPathConfig.STOCK_INFO +"/" + g_day)
      HdfsFileUtil.writeStockCode(HdfsPathConfig.STOCK_INFO + "/" + g_day, stockCodes)
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
