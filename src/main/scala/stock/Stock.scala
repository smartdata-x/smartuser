package stock

/**
  * Created by yangshuai on 2016/1/15.
  */
class Stock(
  val name:String ,// arr(0)//股票名称
  val todayOpeningPrice: Float ,// arr(1).toFloat//今日开盘价
  val yesterdayClosingPrice: Float ,// arr(2).toFloat//昨日收盘价
  val currentPrice: Float ,// arr(3).toFloat//当前价格
  val todayHighestPrice: Float ,// arr(4).toFloat//今日最高价
  val todayLowestPrice: Float ,// arr(5).toFloat//今日最低价
  val highestBuyPrice: Float ,// arr(6).toFloat//竞买价，即“买一”报价
  val lowestSellPrice: Float ,// arr(7).toFloat//竞卖价，即“卖一”报价
  val transactionNumber: Long ,// arr(8).toLong//成交的股票数
  val transactionMoney: Long ,// arr(9).toLong//成交金额
  val highestBuyNumber: Long ,// arr(10).toLong//买一申请数
  val secondHighestBuyNumber: Long ,// arr(12).toLong//买二申请数
  val secondHighestBuyPrice: Float ,// arr(13).toFloat//买二报价
  val thirdHighestBuyNumber: Long ,// arr(14).toLong//买三申请数
  val thirdHighestBuyPrice: Float ,// arr(15).toFloat//买三报价
  val fourthHighestBuyNumber: Long ,// arr(16).toLong//买四申请数
  val fourthHighestBuyPrice: Float ,// arr(17).toFloat//买四报价
  val fifthHighestBuyNumber: Long ,// arr(18).toLong//买五申请数
  val fifthHighestBuyPrice: Float ,// arr(19).toFloat//买五报价
  val lowestBuyNumber: Long ,// arr(20).toLong//卖一报价
  val secondLowestBuyNumber: Long ,// arr(22).toLong//卖二申请数
  val secondLowestBuyPrice: Float ,// arr(23).toFloat//卖二报价
  val thirdLowestBuyNumber: Long ,// arr(24).toLong//卖三申请数
  val thirdLowestBuyPrice: Float ,// arr(25).toFloat//卖三报价
  val fourthLowestBuyNumber: Long ,// arr(26).toLong//卖四申请数
  val fourthLowestBuyPrice: Float ,// arr(27).toFloat//卖四报价
  val fifthLowestBuyNumber: Long ,// arr(28).toLong//卖五申请数
  val fifthLowestBuyPrice: Float ,// arr(29).toFloat//卖五报价
  val date: String ,// arr(30)//日期
  val time: String // arr(31)//时间
  ) {

  override def toString: String = {
    ""
  }

}
