package analysis

import org.apache.hadoop.hbase.client._
import org.apache.spark.{SparkConf, SparkContext}


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
    tableOne.dataAnalysis(sparkConf,sc,1)
    sc.stop()
  }
}
