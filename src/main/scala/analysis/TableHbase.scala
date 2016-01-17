package analysis

import java.util.regex.Pattern

import log.SULogger
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.spark.SparkContext
import util.{HdfsFileUtil, TimeUtil}

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/15.
  * Hbase 操作子类，用来操作Hbase中的表1
  */
class TableHbase extends HBase{

  def get(rowKey:String,table:String,columnFamliy:String,column:String):Result = {
    val connection = ConnectionFactory.createConnection(HBaseConfiguration.create())
    val htable = connection.getTable(TableName.valueOf("1"))
    val get = new Get(Bytes.toBytes(rowKey))
    val resultOfGet = htable.get(get)
    resultOfGet
  }

  def parseDocument(sc:String):mutable.MutableList[String] ={
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

  def getStockCodesFromHbase(sc:SparkContext, timeRange:Int): mutable.MutableList[String] = {
    /** get hbase data */
    val scan = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - timeRange * 60 * 60 * 1000,currentTimeStamp)
    val conf = this.getConfigure(this.tableName,this.columnFamliy,this.column)
    this.setScan(scan)

    val hbaseRdd = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])
    val collectResult = hbaseRdd.collect()

    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser/hbasedata")
    var g_day = new String
    var stockCodes = new mutable.MutableList[String]
    collectResult.foreach(x => {
      try {
        val result = x._2
        val value = Bytes.toString(result.getValue(Bytes.toBytes(this.columnFamliy), Bytes.toBytes(this.column)))
        val timeStamp = result.getColumnLatestCell(Bytes.toBytes(this.columnFamliy), Bytes.toBytes(this.column)).getTimestamp
        val days = TimeUtil.getDayAndHour(String.valueOf(timeStamp))
        g_day = days
        val followList = this.parseDocument(value)
        val userId = this.getUserId(value)
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


}
