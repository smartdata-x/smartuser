package data

import java.util.regex.Pattern

import log.UILogger
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkContext
import scheduler.Scheduler

import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/26.
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
    * 解析用户关注的所有股票代码
    */
  def getAllStockCodes(response: String): ListBuffer[String] ={
    val followStockCodeList = ListBuffer[String]()
    val pattern = "(?<=\")\\d{6}(?=\")".r
    val iterator = pattern.findAllMatchIn(response)

    while(iterator.hasNext) {
      val item = iterator.next
      followStockCodeList.+=(item.toString)
    }

    followStockCodeList.distinct
  }

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

  /**
    * 读取用户自选股信息
    */
  def readUserInfo(sc:SparkContext): Unit = {

    val scan = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - 60 * 60 * 1000,currentTimeStamp)
    val conf = sinaTable.getConfigure(sinaTable.tableName,sinaTable.columnFamily,sinaTable.column)
    sinaTable.setScan(scan)

    val users = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])
    UILogger.warn("Total user number: " + users.count.toString)

    users.foreach(x => {
      try {
        val result = x._2
        val value = Bytes.toString(result.getValue(Bytes.toBytes(sinaTable.columnFamily), Bytes.toBytes(sinaTable.column)))
        val userId = getUserId(value)
        val userStockList = getAllStockCodes(value)
        if (userStockList.nonEmpty)
          Scheduler.userMap.put(userId, userStockList)
      } catch {
        case e:Exception =>
          UILogger.exception(e)
      }
    })

  }

  /**
    * 读取用户自选股信息
    */
  def readUserInfo(sc:SparkContext, ts:Long): Unit = {

    val scan = new Scan()
    val currentTimeStamp = ts
    scan.setTimeRange(currentTimeStamp - 60 * 60 * 1000,currentTimeStamp)
    val conf = sinaTable.getConfigure(sinaTable.tableName,sinaTable.columnFamily,sinaTable.column)
    sinaTable.setScan(scan)

    val users = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])
    UILogger.warn("Total user number: " + users.count.toString)

    users.foreach(x => {
      try {
        val result = x._2
        val value = Bytes.toString(result.getValue(Bytes.toBytes(sinaTable.columnFamily), Bytes.toBytes(sinaTable.column)))
        val userId = getUserId(value)
        val userStockList = getAllStockCodes(value)
        if (userStockList.nonEmpty)
          Scheduler.userMap.put(userId, userStockList)
      } catch {
        case e:Exception =>
          UILogger.exception(e)
      }
    })
  }
}
