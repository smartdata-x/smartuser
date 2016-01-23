package calculate.user

import stock.{RateOfReturn, UserStock}

/**
  * Created by kerry on 16/1/19.
  * 计算每个用户回报率如果为负数则返回false
  */
class UserStrategyOne extends UserRateOfReturn{


  def countUserTotalRateOfReturn(u:RateOfReturn) = {

  }

  override def calculate(u:UserStock):Boolean = {
    var currentTotalRateOfReturn = 0.0f
    // val mapValues = u._hash.mapValues(countUserTotalRateOfReturn(_))
    u._hash.foreach(x =>{
      currentTotalRateOfReturn += x._2.getRateOfReturn
      // println("user:stockcode:"+u.getCode+",rateOfReturn:"+u.getRateOfReturn)
    })
    println("user:"+u.getUid()+",mapValues:"+u._hash.size.toFloat+",currentTotalRateOfReturn:"+currentTotalRateOfReturn)
    u.setCurrentRate((currentTotalRateOfReturn/u._hash.size.toFloat))
    println("user:currentRate:"+u.getCurrentRate())
    if (u.getCurrentRate() > 0)
      true
    else
      true
  }
}

