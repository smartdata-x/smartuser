package stock

/**
  * Created by yangshuai on 2016/1/15.
  */
class Stock(
  val code:String, //股票代碼
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

  override def toString(): String ={

    code+"\t"+name+"\t"+todayOpeningPrice+"\t"+yesterdayClosingPrice+"\t"+currentPrice+"\t"+todayHighestPrice+"\t"+ todayLowestPrice+"\t"+
    transactionNumber+"\t"+transactionMoney+"\t"+highestBuyNumber+"\t"+highestBuyPrice+"\t"+ secondHighestBuyNumber+"\t"+secondHighestBuyPrice+"\t"+ thirdHighestBuyNumber+"\t"+
    thirdHighestBuyPrice+"\t"+fourthHighestBuyNumber+"\t"+fourthHighestBuyPrice+"\t"+fifthHighestBuyNumber+"\t"+ fifthHighestBuyPrice+"\t"+lowestBuyNumber+"\t"+ lowestSellPrice+"\t"+
    secondLowestBuyNumber+"\t"+secondLowestBuyPrice+"\t"+thirdLowestBuyNumber+"\t"+thirdLowestBuyPrice+"\t"+ fourthLowestBuyNumber+"\t"+fourthLowestBuyPrice+"\t"+ fifthLowestBuyNumber+"\t"+
    fifthLowestBuyPrice+"\t"+date+"\t"+time
  }
}

object Stock {

  def apply(line: String): Stock = {
    val arr = line.split("\t")
    new Stock(arr(0), arr(1), arr(2).toFloat, arr(3).toFloat, arr(4).toFloat, arr(5).toFloat, arr(6).toFloat, arr(7).toLong, arr(8).toFloat, arr(9).toLong,
      arr(10).toFloat, arr(11).toLong, arr(12).toFloat, arr(13).toLong, arr(14).toFloat, arr(15).toLong, arr(16).toFloat, arr(17).toLong, arr(18).toFloat, arr(19).toLong,
      arr(20).toFloat, arr(21).toLong, arr(22).toFloat, arr(23).toLong, arr(24).toFloat, arr(25).toLong, arr(26).toFloat, arr(27).toLong, arr(28).toFloat, arr(29), arr(30))
  }

  def getTypeOfStockCode(code:String): String ={
    var validStockCode = new String
    val head = code.charAt(0)
    if (head == '0' || head == '3') {
      validStockCode += "sz" + code
    } else if (head == '6' || head == '9') {
      validStockCode += "sh" + code
    }
    validStockCode
  }
}
