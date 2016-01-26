package config

/**
  * Created by yangshuai on 2016/1/26.
  */
object SparkConfig {
  val SPARK_SERIALIZER = "org.apache.spark.serializer.KryoSerializer"
  val SPARK_KRYOSERIALIZER_BUFFER_MAX = "2000"
}
