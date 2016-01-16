package calculate

import stock.Stock


/**
  * Created by yangshuai on 2016/1/15
  * 回报率计算策略
  */
trait RateOfReturnStrategy {

  def calculate(s1:Stock, s2:Stock):Float

}

object RateOfReturnStrategy {

  private class Strategy1 extends RateOfReturnStrategy {

    override def calculate(s1:Stock, s2:Stock): Float = {
      1f
    }
  }

  private class Strategy2 extends RateOfReturnStrategy {

    override def calculate(s1:Stock, s2:Stock): Float = {
      2f
    }
  }

  private class Strategy3 extends RateOfReturnStrategy {

    override def calculate(s1:Stock, s2:Stock): Float = {
      3f
    }
  }

  private class Strategy4 extends RateOfReturnStrategy {

    override def calculate(s1:Stock, s2:Stock): Float = {
      4f
    }
  }

  def apply(sType: Int): RateOfReturnStrategy = {
    if (sType == 1) {
      new Strategy1
    } else if (sType == 2) {
      new Strategy2
    } else if (sType == 3) {
      new Strategy3
    } else {
      new Strategy4
    }
  }
}
