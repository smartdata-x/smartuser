package analysis

import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}
import util.{HdfsFileUtil, TimeUtil}

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/13.
  */
object DataAnalysis {

  def MergeList(globle_list: mutable.MutableList[String],temp_list:mutable.MutableList[String]): mutable.MutableList[String] ={
    if(temp_list != null) {
      val iterator = temp_list.iterator
      while (iterator.hasNext) {
        val value = iterator.next()
        if (!globle_list.contains(value)) {
          globle_list.+=(value)
        }
      }
    }
    globle_list
  }

  def main(args: Array[String]) {

    val  SparkConf = new SparkConf()
      .setAppName("DataAnalysis")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryoserializer.buffer.max", "2000")
      .setMaster("local")

    val sc = new SparkContext(SparkConf)
    /** get hbase data */
    var one = new TableOneHbase
    one.tableName_=("1")
    one.columnFamliy_=("basic")
    one.column_=("content")

    val scan = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - 3600000,currentTimeStamp)
    val conf = one.getConfigure(one.tableName,one.columnFamliy,one.column)
    one.setScan(scan)

    val hbaseRdd = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[Result])
    val collectResult = hbaseRdd.collect()
    val logger = Logger.getRootLogger

    HdfsFileUtil.setHdfsUri("hdfs://server:9000")
    HdfsFileUtil.setRootDir("smartuser/hbasedata")
    var g_day = new String
    var stockCodes = new mutable.MutableList[String]
    collectResult.foreach(x => {
      try {
      val result = x._2
      val value = Bytes.toString(result.getValue(Bytes.toBytes(one.columnFamliy), Bytes.toBytes(one.column)))
      val timeStamp = result.getColumnLatestCell(Bytes.toBytes(one.columnFamliy), Bytes.toBytes(one.column)).getTimestamp
      val days = TimeUtil.getDayAndHour(String.valueOf(timeStamp))
      g_day = days
      val followList = one.parseDocument(value)
      val userId = one.getUserId(value)
      /** HDFS 操作*/
      val currentPath = HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+days)
      if(userId != null && followList !=null){
        stockCodes = MergeList(stockCodes,followList)
        HdfsFileUtil.mkFile(currentPath+userId)
        // System.out.println("rowKey----"+rowKey)
        HdfsFileUtil.writeStockCode(currentPath + userId,followList)
      }
      } catch {
        case e:Exception => println("[C.J.YOU] writeToHdfsFile error")
          logger.error("[C.J.YOU]"+e.printStackTrace)
      }
    })
    /** 保存全局股票代码 */
    HdfsFileUtil.mkDir(HdfsFileUtil.getRootDir+""+"stockCodes")
    HdfsFileUtil.mkFile(HdfsFileUtil.getRootDir +"stockCodes"+"/"+g_day)
    HdfsFileUtil.writeStockCode(HdfsFileUtil.getRootDir +"stockCodes"+"/"+g_day,stockCodes)
    sc.stop()
  }
}
