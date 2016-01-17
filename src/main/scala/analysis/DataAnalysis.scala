package analysis

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
    val tableOne = new TableHbase
    tableOne.getStockCodesFromHbase(sc,1)
    sc.stop()
  }
}
