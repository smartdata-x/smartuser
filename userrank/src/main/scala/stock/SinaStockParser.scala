package stock

import log.URLogger

import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/18.
  */
class SinaStockParser extends StockParser {

  val NAME = 0//股票名称
  val TODAYOPENINGPRICE = 1//今日开盘价
  val YESTERDAYCLOSINGPRICE = 2//昨日收盘价
  val CURRENTPRICE = 3//当前价格

  val TODAYHIGHESTPRICE = 4//今日最高价
  val TODAYLOWESTPRICE = 5//今日最低价
  val TRANSACTIONNUMBER = 8//成交的股票数
  val TRANSACTIONMONEY = 9//成交金额

  val HIGHESTBUYNUMBER = 10//买一申请数
  val HIGHESTBUYPRICE = 6//竞买价，即“买一”报价
  val SECONDHIGHESTBUYNUMBER = 12//买二申请数
  val SECONDHIGHESTBUYPRICE = 13//买二报价
  val THIRDHIGHESTBUYNUMBER = 14//买三申请数
  val THIRDHIGHESTBUYPRICE = 15//买三报价
  val FOURTHHIGHESTBUYNUMBER = 16//买四申请数
  val FOURTHHIGHESTBUYPRICE = 17//买四报价
  val FIFTHHIGHESTBUYNUMBER = 18//买五申请数
  val FIFTHHIGHESTBUYPRICE = 19//买五报价

  val LOWESTBUYNUMBER = 20//卖一申请数
  val LOWESTSELLPRICE = 7//竞卖价，即“卖一”报价
  val SECONDLOWESTBUYNUMBER = 22//卖二申请数
  val SECONDLOWESTBUYPRICE = 23//卖二报价
  val THIRDLOWESTBUYNUMBER = 24//卖三申请数
  val THIRDLOWESTBUYPRICE = 25//卖三报价
  val FOURTHLOWESTBUYNUMBER = 26//卖四申请数
  val FOURTHLOWESTBUYPRICE = 27//卖四报价
  val FIFTHLOWESTBUYNUMBER = 28//卖五申请数
  val FIFTHLOWESTBUYPRICE = 29//卖五报价

  val DATE = 30// ARR(30)//日期
  val TIME = 31// ARR(31)//时间

  override def parseStockList(stockCodes: Array[String], content: String): ListBuffer[Stock] = {

    var stockList = new ListBuffer[Stock]()

    val contentArr = content.split("\n")

    for (i <- stockCodes.indices) {
      if (contentArr(i).length > 30) {
        val stock = parseStock(contentArr(i), stockCodes(i))
        stockList += stock
      }
    }

    stockList
  }

  override def parseStock(response: String, code: String): Stock = {

    val pattern = "(?<==\").*(?=\")".r
    val arr = pattern.findFirstIn(response).get.split(",")

    new Stock(code,
      arr(NAME), arr(TODAYOPENINGPRICE).toFloat, arr(YESTERDAYCLOSINGPRICE).toFloat, arr(CURRENTPRICE).toFloat,
      arr(TODAYHIGHESTPRICE).toFloat, arr(TODAYLOWESTPRICE).toFloat, arr(TRANSACTIONNUMBER).toLong, arr(TRANSACTIONMONEY).toFloat,
      arr(HIGHESTBUYNUMBER).toLong, arr(HIGHESTBUYPRICE).toFloat,arr(SECONDHIGHESTBUYNUMBER).toLong, arr(SECONDHIGHESTBUYPRICE).toFloat, arr(THIRDHIGHESTBUYNUMBER).toLong, arr(THIRDHIGHESTBUYPRICE).toFloat, arr(FOURTHHIGHESTBUYNUMBER).toLong, arr(FOURTHHIGHESTBUYPRICE).toFloat, arr(FIFTHHIGHESTBUYNUMBER).toLong, arr(FIFTHHIGHESTBUYPRICE).toFloat,
      arr(LOWESTBUYNUMBER).toLong, arr(LOWESTSELLPRICE).toFloat, arr(SECONDLOWESTBUYNUMBER).toLong, arr(SECONDLOWESTBUYPRICE).toFloat, arr(THIRDLOWESTBUYNUMBER).toLong, arr(THIRDLOWESTBUYPRICE).toFloat, arr(FOURTHLOWESTBUYNUMBER).toLong, arr(FOURTHLOWESTBUYPRICE).toFloat, arr(FIFTHLOWESTBUYNUMBER).toLong, arr(FIFTHLOWESTBUYPRICE).toFloat,
      arr(DATE), arr(TIME))
  }
}
