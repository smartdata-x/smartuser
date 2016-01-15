package net

import stock.Stock

import scala.collection.mutable.HashMap

/**
  * Created by yangshuai on 2016/1/15.
  * 腾讯api请求
  */
object TencentRequest extends BaseApiRequest {

  val URL = "http://qt.gtimg.cn/"

  def getRequest(requestParameter:HashMap[String,String]): Unit ={
    super.getRequest(URL, requestParameter)
  }

  override def parse(response: String): Stock = {
    null
  }
}
