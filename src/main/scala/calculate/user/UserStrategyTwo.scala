package calculate.user

import stock.UserStock

/**
  * Created by kerry on 16/1/19.
  */
class UserStrategyTwo extends UserRateOfReturn {
  override def calculate(u: UserStock): Boolean = {
    return true
  }
}
