package calculate.stock

import stock.{RateOfReturn, Stock}


/**
  * Created by kerry on 16/1/16.
  * 计算公式为 value = 1 *(close - open) / open
  * 因为A股市场的情况是(T+1)
  * 故第三天的收盘价-第二天的开盘价是一天的涨幅
  * 不再分三个时段,节假日跳过
  * t为第二天开盘价(9:30),r为第三天的收盘价(15:00)
  */
class StockStrategyTwo extends RateOfReturnStrategy {

  override def calculate(t:Stock, r:Stock): RateOfReturn = {
    val rateOfReturn:RateOfReturn = new RateOfReturn(t.code,t.name)
    val rate = 1 * (r.currentPrice - t.todayOpeningPrice) / t.todayOpeningPrice
    rateOfReturn.current_rate_(rate)
    //println(rateOfReturn.getCode,rateOfReturn.getName,rate)
    rateOfReturn
  }
}

