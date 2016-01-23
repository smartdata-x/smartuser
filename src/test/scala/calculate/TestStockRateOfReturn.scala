package calculate

import _root_.stock.{UserStock, RateOfReturn, Stock}
import calculate.user.UserRateOfReturnStrategy
import config.{FileConfig, StrategyConfig}
import org.scalatest.{Matchers, FlatSpec}
import calculate.stock.RateOfReturnStrategy
import data.FileUtil

import scala.collection.mutable

/**
  * Created by kerry on 16/1/22.
  */
class TestStockRateOfReturn  extends  FlatSpec with Matchers{

  val startTimeHash = mutable.HashMap[String, Stock]()
  val endTimeHash = mutable.HashMap[String, Stock]()
  val returnOfReturn = mutable.HashMap[String,RateOfReturn]()
  val user = mutable.MutableList[UserStock]()
  def createStockCodeUnit(content:Any):Stock = {
    val arr = content.toString.split("\t")
    new Stock(arr(0), arr(1), arr(2).toFloat, arr(3).toFloat, arr(4).toFloat, arr(5).toFloat,
      arr(6).toFloat, arr(7).toLong, arr(8).toFloat, arr(9).toLong, arr(10).toFloat, arr(11).toLong,
      arr(12).toFloat, arr(13).toLong, arr(14).toFloat, arr(15).toLong, arr(16).toFloat, arr(17).toLong,
      arr(18).toFloat, arr(19).toLong, arr(20).toFloat, arr(21).toLong, arr(22).toFloat, arr(23).toLong,
      arr(24).toFloat, arr(25).toLong, arr(26).toFloat, arr(27).toLong, arr(28).toFloat, arr(29), arr(30))
  }

  def createCodeUnitStartTime(content: Any):Unit = {
    startTimeHash.put(createStockCodeUnit(content).code, createStockCodeUnit(content))
  }

  def createCodeUnitEndTime(content: Any):Unit = {
    endTimeHash.put(createStockCodeUnit(content).code, createStockCodeUnit(content))
  }


  def createGrailGo(path:String,stype:Int) = {
    val grailGoTime = FileUtil.readFile(path)
    if (stype == 1)
      grailGoTime.foreach(createCodeUnitStartTime)
    else
      grailGoTime.foreach(createCodeUnitEndTime)

  }

  "StockRateOfReturn class method function " should "work" in {
    val stockRate = RateOfReturnStrategy(StrategyConfig.STRATEGY_ONE)
    val userRate = UserRateOfReturnStrategy(StrategyConfig.STRATEGY_ONE)
    val detsPath = FileConfig.STOCK_INFO
    createGrailGo("src/test/resources/stock/2016-01-20/9",1)
    createGrailGo("src/test/resources/stock/2016-01-20/13",2)

    /*
      单股回报率
     */
    startTimeHash.foreach(
      x => {
        val codeStock =  x._1
        val stockT = x._2
        if(endTimeHash.contains(codeStock)) {
          val stockR = endTimeHash.get(codeStock).get
          returnOfReturn.put(codeStock, stockRate.calculate(stockT, stockR))
        }
      }
    )

    /*
      用户回报率
     */
    val userStockCodes  = FileUtil.getUserStockInfo("src/test/resources/user/2016-01-20_15")
    userStockCodes.foreach(x =>{
      val userRateOfReturn = mutable.HashMap[String,RateOfReturn]()
      val userStock = new UserStock()
      val userId = x._1
      println("uid:"+userId)
      userStock.setUid(userId)
      val stockCodeListBuffer = x._2
      stockCodeListBuffer.foreach( y => {
        if(returnOfReturn.contains(y)){
          val rate = returnOfReturn.get(y).get
          println("code:"+y+",rate:"+rate.getRateOfReturn)
          userRateOfReturn.+=((y,rate))
        }
      })
      userStock.setStockes(userRateOfReturn)
      userRate.calculate(userStock)
      user.+=(userStock)
    })

    user.foreach(x =>{
      println("uid:"+x.getUid()+",rate:"+x.getCurrentRate())
    })
  }
}
