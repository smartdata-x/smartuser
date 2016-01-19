package calculate.user

import stock.UserStock

/**
  * Created by kerry on 16/1/19.
  * 计算每个用户回报率如果为负数则返回false
  */
class UserStrategyOne extends UserRateOfReturn{
  override def calculate(u:UserStock):Boolean = {
    return true
  }
}
