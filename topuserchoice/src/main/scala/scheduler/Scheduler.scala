package scheduler

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

import config.RedisConfig
import log.TUCLogger
import org.apache.spark.{SparkConf, SparkContext}
import org.w3c.dom.Element
import redis.clients.jedis.Jedis
import util.{FileUtil, TimeUtil}

import scala.collection.mutable

/**
  * Created by yangshuai on 2016/1/27.
  */
object Scheduler {

  var preUserMap = mutable.Map[String, Set[String]]()
  var topUsers = mutable.ListBuffer[String]()

  val conf =  new SparkConf().setAppName("TOP_USER_CHOICE")
  val sc = new SparkContext(conf)

  def main(args: Array[String]): Unit = {

    initConfiguration(args(0))

    topUsers = FileUtil.readTopUserList
    TUCLogger.warn("Top user number: " + topUsers.size)
    preUserMap = FileUtil.readUserInfoByDayAndHour(-1, 15)
    val curUserMap = FileUtil.readUserInfoByDayAndHour(0, 9)

    val result = sc.parallelize(curUserMap.toSeq)
      .filter(_._2.nonEmpty)
      .flatMap(x => getNewStocks(x._1, x._2))
      .map((_,1))
      .reduceByKey(_+_)
      .sortBy(_._2, ascending = false)
      .map(x => x._1 + "\t" + (x._2 * 1.0 / topUsers.size))
      .collect

    sendNewStockPercent(result)

    sc.stop
  }

  def getNewStocks(userId: String, set: Set[String]): Set[String] = {

    if (!topUsers.contains(userId))
      return Set[String]()

    val preSet = preUserMap.get(userId)
    if (preSet.isEmpty)
      return Set[String]()

    set.--(preSet.get)
  }

  /**
    * 计算新增自选股所占比率并存到redis
    */
  def sendNewStockPercent(userList: Seq[String]): Unit = {

    val jedis = new Jedis(RedisConfig.ip, RedisConfig.port)
    jedis.auth(RedisConfig.auth)
    val pipeline = jedis.pipelined()

    TUCLogger.warn("Message number: " + userList.size)

    userList.map(x => {
      val arr = x.split("\t")
      pipeline.zadd("newstock:" + TimeUtil.getDay, arr(1).toDouble, arr(0))
      pipeline.expire("newstock:" + TimeUtil.getDay, 60 * 60 * 48)
    })

    pipeline.sync()
    jedis.quit
  }

  def initConfiguration(path: String): Unit = {

    val file = new File(path)

    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
    val redisRoot = document.getElementsByTagName("redis").item(0).asInstanceOf[Element]
    val ip = redisRoot.getElementsByTagName("ip").item(0).getTextContent
    val port = redisRoot.getElementsByTagName("port").item(0).getTextContent
    val auth = redisRoot.getElementsByTagName("auth").item(0).getTextContent

    RedisConfig.init(ip, port.toInt, auth)
  }

}
