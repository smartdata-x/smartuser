package stock

/**
  * Created by yangshuai on 2016/1/15.
  */
class Stock(
  val name:String ,//股票名称
  val todayOpeningPrice: Float ,//今日开盘价
  val yesterdayClosingPrice: Float ,//昨日收盘价
  val currentPrice: Float ,//当前价格

  val todayHighestPrice: Float ,//今日最高价
  val todayLowestPrice: Float ,//今日最低价
  val transactionNumber: Long ,//成交的股票数
  val transactionMoney: Float ,//成交金额

  val highestBuyNumber: Long ,//买一申请数
  val highestBuyPrice: Float ,//竞买价，即“买一”报价
  val secondHighestBuyNumber: Long ,//买二申请数
  val secondHighestBuyPrice: Float ,//买二报价
  val thirdHighestBuyNumber: Long ,//买三申请数
  val thirdHighestBuyPrice: Float ,//买三报价
  val fourthHighestBuyNumber: Long ,//买四申请数
  val fourthHighestBuyPrice: Float ,//买四报价
  val fifthHighestBuyNumber: Long ,//买五申请数
  val fifthHighestBuyPrice: Float ,//买五报价

  val lowestBuyNumber: Long ,//卖一申请数
  val lowestSellPrice: Float ,//竞卖价，即“卖一”报价
  val secondLowestBuyNumber: Long ,//卖二申请数
  val secondLowestBuyPrice: Float ,//卖二报价
  val thirdLowestBuyNumber: Long ,//卖三申请数
  val thirdLowestBuyPrice: Float ,//卖三报价
  val fourthLowestBuyNumber: Long ,//卖四申请数
  val fourthLowestBuyPrice: Float ,//卖四报价
  val fifthLowestBuyNumber: Long ,//卖五申请数
  val fifthLowestBuyPrice: Float ,//卖五报价

  val date: String,// arr(30)//日期
  val time: String // arr(31)//时间
  ) {

  override def toString: String = {
    ""
  }

}
