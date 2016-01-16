package analysis

import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}
import util.{TimeUtil, HdfsFileUtil}

/**
  * Created by C.J.YOU on 2016/1/13.
  */
object DataAnalysis {
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

    val fileUtil = new HdfsFileUtil
    fileUtil.setHdfsUri("hdfs://server:9000")
    fileUtil.setRootDir("smartuser")
    collectResult.foreach(x => {
      try {
      val result = x._2
      val value = Bytes.toString(result.getValue(Bytes.toBytes(one.columnFamliy), Bytes.toBytes(one.column)))
      val timeStamp = result.getColumnLatestCell(Bytes.toBytes(one.columnFamliy), Bytes.toBytes(one.column)).getTimestamp
      val rowKey = Bytes.toString(result.getColumnLatestCell(Bytes.toBytes(one.columnFamliy), Bytes.toBytes(one.column)).getRow)
      val days = TimeUtil.GetDate(String.valueOf(timeStamp))
      val followList = one.parseDocument(value)
      val userId = one.getUserId(value)
      /** HDFS 操作*/
      val currentPath = fileUtil.mkDir(fileUtil.getRootDir()+days)
      if(userId != null && followList !=null){
        fileUtil.mkFile(currentPath+userId)
        // System.out.println("rowKey----"+rowKey)
        fileUtil.writeToFile(currentPath + userId,followList,rowKey)
      }
      } catch {
        case e:Exception => println("[C.J.YOU]writeToHdfsFile error")
          logger.error("[C.J.YOU]"+e.printStackTrace)
      }
    })
    sc.stop()
  }
}
