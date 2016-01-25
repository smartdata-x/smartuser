package stock

import config.StockConfig

import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/18.
  */
trait StockParser {

  def parseStockList(stockCodes: Array[String], content: String): ListBuffer[Stock]

  def parseStock(response: String, code: String): Stock
}

object StockParser {

  def apply(platform: Int): StockParser = {

    if (platform == StockConfig.SINA) {
      return new SinaStockParser
    }
    null
  }

  def validCode(code: String): Boolean = {
    if (code.length == 0)
      return false
    val head = code.charAt(0)
    if (head == '0' || head == '3' || head == '6' || head == '9')
      return true
    false
  }
}
