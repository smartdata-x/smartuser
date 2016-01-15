package net

import config.URLConfig
import stock.Stock

import scala.collection.mutable
import scala.collection.mutable.HashMap

/**
  * Created by yangshuai on 2016/1/15.
  * 腾讯api请求
  */
object TencentRequest extends BaseHttp {

  val NAME = 1//股票名称
  val TODAYOPENINGPRICE = 5//今日开盘价
  val YESTERDAYCLOSINGPRICE = 4//昨日收盘价
  val CURRENTPRICE = 3//当前价格

  val TODAYHIGHESTPRICE = 33//今日最高价
  val TODAYLOWESTPRICE = 34//今日最低价
  val TRANSACTIONNUMBER = 36//成交的股票数
  val TRANSACTIONMONEY = 37//成交金额

  val HIGHESTBUYNUMBER = 10//买一申请数
  val HIGHESTBUYPRICE = 9//竞买价，即“买一”报价
  val SECONDHIGHESTBUYNUMBER = 12//买二申请数
  val SECONDHIGHESTBUYPRICE = 11//买二报价
  val THIRDHIGHESTBUYNUMBER = 14//买三申请数
  val THIRDHIGHESTBUYPRICE = 13//买三报价
  val FOURTHHIGHESTBUYNUMBER = 16//买四申请数
  val FOURTHHIGHESTBUYPRICE = 15//买四报价
  val FIFTHHIGHESTBUYNUMBER = 18//买五申请数
  val FIFTHHIGHESTBUYPRICE = 17//买五报价

  val LOWESTBUYNUMBER = 20//卖一申请数
  val LOWESTSELLPRICE = 19//竞卖价，即“卖一”报价
  val SECONDLOWESTBUYNUMBER = 22//卖二申请数
  val SECONDLOWESTBUYPRICE = 21//卖二报价
  val THIRDLOWESTBUYNUMBER = 24//卖三申请数
  val THIRDLOWESTBUYPRICE = 23//卖三报价
  val FOURTHLOWESTBUYNUMBER = 26//卖四申请数
  val FOURTHLOWESTBUYPRICE = 25//卖四报价
  val FIFTHLOWESTBUYNUMBER = 28//卖五申请数
  val FIFTHLOWESTBUYPRICE = 27//卖五报价

  val DATE = 30//日期

  def sendRequest(requestParameter:mutable.HashMap[String,String]): Unit ={
    request(URLConfig.tencent, requestParameter, parse)
  }

  def parse(response: String): Unit = {

    val pattern = "(?<==\").*(?=\")".r
    val arr = pattern.findFirstIn(response).get.split("~")

    val date = arr(DATE).substring(0, 4) + "-" + arr(DATE).substring(4, 6) + "-" + arr(DATE).substring(6, 8)
    val time = arr(DATE).substring(8, 10) + ":" + arr(DATE).substring(10, 12) + ":" + arr(DATE).substring(12, 14)

    val stock = new Stock(
      arr(NAME), arr(TODAYOPENINGPRICE).toFloat, arr(YESTERDAYCLOSINGPRICE).toFloat, arr(CURRENTPRICE).toFloat,
      arr(TODAYHIGHESTPRICE).toFloat, arr(TODAYLOWESTPRICE).toFloat, arr(TRANSACTIONNUMBER).toLong * 100, arr(TRANSACTIONMONEY).toFloat * 10000,
      arr(HIGHESTBUYNUMBER).toLong * 100, arr(HIGHESTBUYPRICE).toFloat,arr(SECONDHIGHESTBUYNUMBER).toLong * 100, arr(SECONDHIGHESTBUYPRICE).toFloat,arr(THIRDHIGHESTBUYNUMBER).toLong * 100, arr(THIRDHIGHESTBUYPRICE).toFloat,arr(FOURTHHIGHESTBUYNUMBER).toLong * 100, arr(FOURTHHIGHESTBUYPRICE).toFloat,arr(FIFTHHIGHESTBUYNUMBER).toLong * 100, arr(FIFTHHIGHESTBUYPRICE).toFloat,
      arr(LOWESTBUYNUMBER).toLong * 100, arr(LOWESTSELLPRICE).toFloat,arr(SECONDLOWESTBUYNUMBER).toLong * 100, arr(SECONDLOWESTBUYPRICE).toFloat,arr(THIRDLOWESTBUYNUMBER).toLong * 100, arr(THIRDLOWESTBUYPRICE).toFloat,arr(FOURTHLOWESTBUYNUMBER).toLong * 100, arr(FOURTHLOWESTBUYPRICE).toFloat,arr(FIFTHLOWESTBUYNUMBER).toLong * 100, arr(FIFTHLOWESTBUYPRICE).toFloat,
      date, time)

    val name = stock.name
    println(name)
  }
}
