package user
import scala.collection.mutable
import stock.Stock

/**
  * Created by kerry on 16/1/17.
  */
class UserStock {
  private var _uid: String = new String
  private val _hash: mutable.HashMap[String, Stock] = new mutable.HashMap[String, Stock]
  private var _currentRate  = 0

  def this(uid : String) = {
    this()
    this._uid = uid
  }

  def setStock(stock: Stock): Unit ={
    this._hash(stock.name) = stock
  }

  def setStockes(stockes:mutable.HashMap[String,String]): Unit ={
    this._hash.++ (stockes)
  }
}
