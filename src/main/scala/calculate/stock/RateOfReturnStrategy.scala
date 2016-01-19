package calculate.stock

import config.StrategyConfig
import stock.{RateOfReturn, Stock}


/**
  * Created by yangshuai on 2016/1/15
  * 单支股票回报率计算策略
  */
trait RateOfReturnStrategy {

  def calculate(t:Stock, r:Stock):RateOfReturn

}

object RateOfReturnStrategy {

  def apply(sType: Int): RateOfReturnStrategy = {
    if (sType == StrategyConfig.STRATEGY_ONE) {
      new StockStrategyOne
    } else  {
      new StockStrategyTwo
    }
  }
}
