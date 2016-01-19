package calculate.user

import stock.{RateOfReturn, UserStock}

/**
  * Created by kerry on 16/1/19.
  * 计算每个用户回报率如果为负数则返回false
  */
class UserStrategyOne extends UserRateOfReturn{

  def countUserTotalRateOfReturn(u:RateOfReturn) = {
    u.getRateOfReturn()
  }

  override def calculate(u:UserStock):Boolean = {
    val mapValues = u._hash.mapValues(countUserTotalRateOfReturn)
    return true
  }
}

