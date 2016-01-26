package util

import org.scalatest.{FlatSpec, Matchers}
import redis.clients.jedis.Jedis

/**
  * Created by yangshuai on 2016/1/26.
  */
class JedisUtilTest extends FlatSpec with Matchers {

  it should "work" in {

    val jedis = new Jedis("222.73.34.96", 6390)
    jedis.auth("7ifW4i@M")
    val pipline = jedis.pipelined()

    for (i <- 0 to 100)
      pipline.hset("test", i.toString, "a")

    pipline.sync()

    println(jedis.quit())
  }
}
