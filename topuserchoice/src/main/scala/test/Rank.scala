package test

import config.RedisConfig
import redis.clients.jedis.Jedis
import scala.collection.JavaConverters._

/**
  * Created by yangshuai on 2016/2/16.
  */
object Rank extends App {

  val jedis = new Jedis("222.73.34.96", 6390)
  jedis.auth("7ifW4i@M")

  val map = jedis.hgetAll("newstock:2016-02-16")

  var myScalaMap = map.asScala.toSeq.sortWith(_._2 > _._2).take(10)
  myScalaMap.foreach(println)



}
