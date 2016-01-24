package calculate.user

import stock.UserStock

/**
  * Created by kerry on 16/1/19.
  * 记录用户一天新增关注股票,将新增股票的回报率除以单个次数
  * 计算每个用户回报率如果为负数则返回false
  */
class UserStrategyTwo extends UserRateOfReturn {
  override def calculate(u: UserStock): Boolean = {
    return true
  }
}
