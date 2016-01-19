package calculate

import stock.Stock
import stock.RateOfReturn


/**
  * Created by yangshuai on 2016/1/15
  * 单支股票回报率计算策略
  */
trait RateOfReturnStrategy {

  def calculate(t:Stock, r:Stock):RateOfReturn

}

object RateOfReturnStrategy {

  def apply(sType: Int): RateOfReturnStrategy = {
    if (sType == StrategyConig.STRATEGY_ONE) {
      new StockStrategyOne
    } else  {
      new StockStrategyTwo
    }
  }
}
