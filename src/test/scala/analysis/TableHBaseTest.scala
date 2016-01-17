package analysis

import org.apache.hadoop.hbase.client.Scan
import org.apache.spark.{SparkContext, SparkConf}
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by C.J.YOU on 2016/1/16.
  */
class TableHBaseTest extends FlatSpec with Matchers {

  "hbase get data and save stockCode method" should "work" in{
    val  sparkConf = new SparkConf()
      .setAppName("DataAnalysis")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryoserializer.buffer.max", "2000")
      .setMaster("local")
    val sc = new SparkContext(sparkConf)
    TableHbase.getStockCodesFromHbase(sc,1)
    sc.stop()

  }
}
