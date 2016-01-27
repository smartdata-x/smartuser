package stock

/**
  * Created by kerry on 16/1/18.
  */
class RateOfReturn {

  private var _code: String = _//股票代碼
  private var _name: String = _//股票名称
  private var _current_rate: Float = _ //回报率
  private var _date: String = _ //回报率的时间段

  def this(code: String, name: String) = {
    this()
    _code = code
    _name = name
    _current_rate = 0f
  }

  def current_rate_(current_rate: Float): Unit ={
    _current_rate = current_rate
  }

  def code: String = _code

  def name: String = _name

  def current_rate: Float =_current_rate

  def date: String = _date

  def date_(date: String): Unit = {
    _date = date
  }
}
