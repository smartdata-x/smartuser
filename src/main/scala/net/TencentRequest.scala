package net

import stock.Stock

import scala.collection.mutable
import scala.collection.mutable.HashMap

/**
  * Created by yangshuai on 2016/1/15.
  * 腾讯api请求
  */
object TencentRequest extends BaseHttp {

  val URL = "http://qt.gtimg.cn/"

  def sendRequest(requestParameter:mutable.HashMap[String,String]): Unit ={
    val response = request(URL, requestParameter)
    parse(response)
  }

  override def parse(response: String): Stock = {

    val pattern = "(?<==\").*(?=\")".r
    val arr = pattern.findFirstIn(response).get.split("~")

    val date = arr(30).substring(0, 4) + "-" + arr(30).substring(4, 6) + "-" + arr(30).substring(6, 8)
    val time = arr(30).substring(8, 10) + ":" + arr(30).substring(10, 12) + ":" + arr(30).substring(12, 14)

    new Stock(
      arr(1), arr(5).toFloat, arr(4).toFloat, arr(3).toFloat,
      arr(33).toFloat, arr(34).toFloat, arr(36).toLong * 100, arr(37).toFloat * 10000,
      arr(10).toLong * 100, arr(9).toFloat,arr(12).toLong * 100, arr(11).toFloat,arr(14).toLong * 100, arr(13).toFloat,arr(16).toLong * 100, arr(15).toFloat,arr(18).toLong * 100, arr(17).toFloat,
      arr(20).toLong * 100, arr(19).toFloat,arr(22).toLong * 100, arr(21).toFloat,arr(24).toLong * 100, arr(23).toFloat,arr(26).toLong * 100, arr(25).toFloat,arr(28).toLong * 100, arr(27).toFloat,
      date, time)
  }
}
