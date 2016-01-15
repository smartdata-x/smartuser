package net

import config.URLConfig
import stock.Stock

import scala.collection.mutable

/**
  * Created by yangshuai on 2016/1/15.
  * 新浪api请求
  */
object SinaRequest extends BaseHttp {

  def sendRequest(requestParameter:mutable.HashMap[String,String]): Unit = {
    request(URLConfig.sina, requestParameter, parse)
  }

  def parse(response: String): Unit = {

    val pattern = "(?<==\").*(?=\")".r
    val arr = pattern.findFirstIn(response).get.split(",")

    val stock = new Stock(
      arr(0), arr(1).toFloat, arr(2).toFloat, arr(3).toFloat,
      arr(4).toFloat, arr(5).toFloat, arr(8).toLong, arr(9).toFloat,
      arr(10).toLong, arr(6).toFloat,arr(12).toLong, arr(13).toFloat, arr(14).toLong, arr(15).toFloat, arr(16).toLong, arr(17).toFloat, arr(18).toLong, arr(19).toFloat,
      arr(20).toLong, arr(7).toFloat, arr(22).toLong, arr(23).toFloat, arr(24).toLong, arr(25).toFloat, arr(26).toLong, arr(27).toFloat, arr(28).toLong, arr(29).toFloat,
      arr(30), arr(31))

    val name = stock.name
    println(name)
  }
}
