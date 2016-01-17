package calculate
import stock.Stock


/**
  * Created by kerry on 16/1/16.
  */
class StockStrategyTwo extends RateOfReturnStrategy {

  override def calculate(t:Stock, r:Stock): Float = {
    2f
  }
}

