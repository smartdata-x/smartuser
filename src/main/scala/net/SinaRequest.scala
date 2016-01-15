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
    new Stock(arr)
  }
}
