package calculate

import stock.Stock
import config.StrategyConfig


/**
  * Created by yangshuai on 2016/1/15
  * 回报率计算策略
  */
trait RateOfReturnStrategy {

  def calculate(t:Float, r:Float):Float

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
