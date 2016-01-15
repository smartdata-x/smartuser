package net

import dispatch.Http
import net.SinaRequest._

import scala.collection.mutable

/**
  * Created by yangshuai on 2016/1/15.
  */
object PengjinTest extends BaseHttp {

  def sendPost(requestParameter:mutable.HashMap[String,String]): Unit = {
    val url = "http://www.penging.com/debt/debtInvestList.do"
    post(url, requestParameter, parse)
  }

  def parse(str: String): Unit = {
    val a = 1
    val b = 2
    println(str)
  }

  def main(args: Array[String]): Unit = {
    val map = mutable.HashMap[String, String]()
    map += "pageNumber" -> "1"
    map += "pageSize" -> "7"

    sendPost(map)
  }
}
