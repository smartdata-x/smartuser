package stock
import scala.collection.mutable

/**
  * Created by kerry on 16/1/17.
  */
class UserStock {
  private var _uid: String = new String
  val _hash: mutable.HashMap[String, RateOfReturn] = new mutable.HashMap[String, RateOfReturn]
  private val _currentRate  = 0.0

  def this(uid : String) = {
    this()
    this._uid = uid
  }

  def setStock(stock: RateOfReturn): Unit ={
    this._hash(stock.getCode()) = stock
  }

  def setStockes(stockes:mutable.HashMap[String,String]): Unit ={
    this._hash.++ (stockes)
  }

  def getCurrentRate():Double = {
    return this._currentRate
  }


}
