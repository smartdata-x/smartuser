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

  def main(args: Array[String]) {
    val  sparkConf = new SparkConf()
      .setAppName("DataAnalysis")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryoserializer.buffer.max", "2000")
      .setMaster("local")
    val sc = new SparkContext(sparkConf)
    val tableOne = new TableOneHbase
    tableOne.DataAnalysis(sparkConf,sc)
    sc.stop()
  }
}
