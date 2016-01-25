package stock
import scala.collection.mutable

/**
  * Created by kerry on 16/1/17.
  */
class UserStock {

  private var _uid: String = _
  var _stocks: mutable.HashMap[String, RateOfReturn] =_
  var _new_stocks: mutable.HashMap[String, RateOfReturn] = _
  private var _currentRate: Float  = _

  def uid_(uid : String) = {
    _uid = uid
  }

  def uid: String =_uid

  def updateStock(stock: RateOfReturn): Unit ={
    _stocks(stock.code) = stock
  }

  def stocks_(stocks:mutable.HashMap[String,RateOfReturn]): Unit ={
    _stocks.++=(stocks)
  }

  def currentRate_(rate:Float) = {
    this._currentRate = rate
  }

  def currentRate():Float = _currentRate
}
