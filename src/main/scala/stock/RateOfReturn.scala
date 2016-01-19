package stock

/**
  * Created by kerry on 16/1/18.
  */
class RateOfReturn {

  private var _code:String = ""//股票代碼
  private var _name:String = ""//股票名称
  private var _current_rate:Double = 0.000 //回报率
  private var _date: String = "" //回报率的时间段

  def this(code :String,name : String) = {
    this()
    this._code = code
    this._name = name
    this._current_rate = 0.000
  }

  def setRateOfReturn(current_rate:Double): Unit ={
    this._current_rate = current_rate
  }

  def getCode:String = {
    this._code
  }

  def getName:String = {
    this._name
  }

  def getRateOfReturn:Double ={
    this._current_rate
  }

  def getRateOfReturnDate:String = {
    this._date
  }

  def setRateOfReturnDate(date: String): Unit = {
    this._date = date
  }
}
