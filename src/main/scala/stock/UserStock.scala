package stock
import scala.collection.mutable

/**
  * Created by kerry on 16/1/17.
  */
class UserStock {
  private var _uid: String = new String
  var _hash: mutable.HashMap[String, RateOfReturn] = new mutable.HashMap[String, RateOfReturn]
  private var _currentRate  = 0.0f

  def setUid(uid : String) = {
    this._uid = uid
  }

  def getUid(): String ={
    this._uid
  }

  def setStock(stock: RateOfReturn): Unit ={
    this._hash(stock.getCode) = stock
  }

  def setStockes(stockes:mutable.HashMap[String,RateOfReturn]): Unit ={
    this._hash.++=(stockes)
  }

  def setCurrentRate(rate:Float) = {
    this._currentRate = rate
  }


  def getCurrentRate():Float = {
    return this._currentRate
  }


}
