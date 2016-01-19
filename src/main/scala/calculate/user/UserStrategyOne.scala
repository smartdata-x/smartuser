package calculate.user

import stock.{RateOfReturn, UserStock}

/**
  * Created by kerry on 16/1/19.
  * 计算每个用户回报率如果为负数则返回false
  */
class UserStrategyOne extends UserRateOfReturn{

  var currentTotalRateOfReturn = 0.0
  def countUserTotalRateOfReturn(u:RateOfReturn) = {
    currentTotalRateOfReturn += u.getRateOfReturn()
  }

  override def calculate(u:UserStock):Boolean = {

    val mapValues = u._hash.mapValues(countUserTotalRateOfReturn)
    u.setCurrentRate(currentTotalRateOfReturn/mapValues.size)
    if (u.getCurrentRate() > 0)
      return true
    else
      return true
  }
}

