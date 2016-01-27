package calculate.stock

import stock.{Stock, RateOfReturn}

/**
  * Created by kerry on 16/1/16.
  * 一共分为三个时间:9:30-11:30  13:00-15:00  9:30-15:00
  * 假设开始时间全部买入,结束时间全部卖出.计算每个股票的回报率
  */
class StockStrategyOne extends RateOfReturnStrategy {

  override def calculate(pre:Stock, cur:Stock): RateOfReturn = {

    val rateOfReturn:RateOfReturn = new RateOfReturn(pre.code,pre.name)

    var prePrice = pre.currentPrice
    if (prePrice == 0)
      prePrice = pre.yesterdayClosingPrice

    var curPrice = cur.currentPrice
    if (curPrice == 0)
      curPrice = cur.yesterdayClosingPrice

    val rate = 1 * (curPrice - prePrice) / prePrice
    rateOfReturn.current_rate_(rate)

    rateOfReturn
  }
}
