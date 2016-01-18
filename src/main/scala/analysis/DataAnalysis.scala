package analysis

import config.SparkConfig
import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by C.J.YOU on 2016/1/13.
  */
object DataAnalysis {

  def main(args: Array[String]) {
    val  sparkConf = new SparkConf()
      .setAppName("DataAnalysis")
      .set("spark.serializer", SparkConfig.SPARK_SERIALIZER)
      .set("spark.kryoserializer.buffer.max", SparkConfig.SPARK_KRYOSERIALIZER_BUFFER_MAX)
      .setMaster("local")
    val sc = new SparkContext(sparkConf)
    TableHbase.getStockCodesFromHbase(sc,1)
    sc.stop()
  }
}
