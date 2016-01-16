package net

import dispatch._,Defaults._
import log.SULogger
import org.scalatest.{Matchers, FlatSpec}

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
  * Created by yangshuai on 2016/1/15.
  */
class BaseHttpTest extends FlatSpec with Matchers {

  object TestHttp extends BaseHttp {

    def sendPost(url: String, map: mutable.HashMap[String, String]):Unit = {

      val a = 1
      val b = 2

      post(url, map, method)
    }

    def method(str: String): Unit = {
      val a = 0
      println(str)
    }
  }

  it should "" in {

    val map = mutable.HashMap[String, String]()
    map += "pageNumber" -> "1"
    map += "pageSize" -> "7"

    val url = "http://www.penging.com/debt/debtInvestList.do"
    TestHttp.sendPost(url, map)
  }
}
