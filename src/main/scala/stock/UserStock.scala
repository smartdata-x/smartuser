package stock
import scala.collection.mutable

/**
  * Created by kerry on 16/1/17.
  */
class UserStock {
  private var _uid: String = new String
  var _hash: mutable.HashMap[String, RateOfReturn] = new mutable.HashMap[String, RateOfReturn]
  private var _currentRate  = 0.0

  def this(uid : String) = {
    this()
    this._uid = uid
  }

  def setStock(stock: RateOfReturn): Unit ={
    this._hash(stock.getCode) = stock
  }

  def setStockes(stockes:mutable.HashMap[String,String]): Unit ={
    this._hash.++ (stockes)
  }

  def setCurrentRate(rate:Double) = {
    this._currentRate = rate
  }


  def getCurrentRate():Double = {
    return this._currentRate
  }


}
