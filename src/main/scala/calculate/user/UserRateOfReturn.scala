package calculate.user

import stock.UserStock
import config.StrategyConfig

/**
  * Created by kerry on 16/1/19.
  * 计算用户的回报率
  */
trait UserRateOfReturn {
  def calculate(u:UserStock):Boolean
}

object UserRateOfReturnStrategy {

  def apply(sType: Int): UserRateOfReturn = {
    if (sType == StrategyConfig.STRATEGY_ONE) {
      new UserStrategyOne
    } else  {
      new UserStrategyTwo
    }
  }
}
