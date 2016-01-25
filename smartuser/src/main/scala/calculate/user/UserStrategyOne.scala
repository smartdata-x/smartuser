package calculate.user

import stock.{RateOfReturn, UserStock}

/**
  * Created by kerry on 16/1/19.
  * 整体关注的所有股票回报率总和除以个数
  * 计算每个用户回报率如果为负数则返回false
  */
class UserStrategyOne extends UserRateOfReturn{


  def countUserTotalRateOfReturn(u:RateOfReturn) = {

  }

  override def calculate(u:UserStock):Boolean = {
    var currentTotalRateOfReturn = 0.0f
    u._stocks.foreach(x =>{
      currentTotalRateOfReturn += x._2.current_rate
    })
    u.currentRate_(currentTotalRateOfReturn / u._stocks.size.toFloat)
    if (u.currentRate() > 0)
      true
    else
      true
  }
}

