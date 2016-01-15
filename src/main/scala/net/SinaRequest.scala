package net

import stock.Stock

import scala.collection.mutable.HashMap

/**
  * Created by yangshuai on 2016/1/15.
  * 新浪api请求
  */
object SinaRequest extends BaseApiRequest {

  val URL = "http://hq.sinajs.cn/"

  def sendRequest(requestParameter:HashMap[String,String]): Unit = {
    getRequest(URL, requestParameter)
  }

  override def parse(response: String): Stock = {
    val pattern = "(?<==\").*(?=\")".r
    val source = pattern.findFirstIn(response).get
    val arr = source.split(",")
    new Stock(arr(0), arr(1).toFloat, arr(2).toFloat, arr(3).toFloat, arr(4).toFloat, arr(5).toFloat, arr(6).toFloat, arr(7).toFloat, arr(8).toLong, arr(9).toLong,
      arr(10).toLong, arr(12).toLong, arr(13).toFloat, arr(14).toLong, arr(15).toFloat, arr(16).toLong, arr(17).toFloat, arr(18).toLong, arr(19).toFloat, arr(20).toLong,
      arr(22).toLong, arr(23).toFloat, arr(24).toLong, arr(25).toFloat, arr(26).toLong, arr(27).toFloat, arr(28).toLong, arr(29).toFloat, arr(30), arr(31))
  }
}
