package util

import stock.Stock

/**
  * Created by yangshuai on 2016/1/16.
  * 股票相关计算工具类
  */
object StockUtil {

  /**
    * 计算回报率
    */
  def getRateOfReturn(oldStock:Stock, newStock:Stock):Float = {
    (newStock.currentPrice - oldStock.currentPrice) / newStock.currentPrice
  }
}
