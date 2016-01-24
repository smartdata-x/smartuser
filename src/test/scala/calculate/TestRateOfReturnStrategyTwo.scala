package calculate

import _root_.stock.{UserStock, RateOfReturn, Stock}
import calculate.stock.RateOfReturnStrategy
import calculate.user.UserRateOfReturnStrategy
import config.StrategyConfig
import org.scalatest.{Matchers, FlatSpec}
import data.FileUtil

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by kerry on 16/1/24.
  */
class TestRateOfReturnStrategyTwo extends  FlatSpec with Matchers{

/**
  * 19号的新增自选股(),需要读取20号的9点开盘价,21号的15点(用13点的数据)收盘价
  */

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
    createGrailGo("src/test/resources/stock/2016-01-20/9",1)
    createGrailGo("src/test/resources/stock/2016-01-21/15",2)
    val stockRate = RateOfReturnStrategy(StrategyConfig.STRATEGY_TWO)
    val userRate = UserRateOfReturnStrategy(StrategyConfig.STRATEGY_ONE)

    /*
  单股回报率
 */
    startTimeHash.foreach(
      x => {
        val codeStock =  x._1
        val stockT = x._2
        if(endTimeHash.contains(codeStock)) {
          val stockR = endTimeHash.get(codeStock).get
          //println(codeStock,":",stockRate.calculate(stockT, stockR).getRateOfReturn)
          returnOfReturn.put(codeStock, stockRate.calculate(stockT, stockR))
        }
      }
    )


    /*
   计算用户新增自选股
  */

    val userStockCodesYesterday  = FileUtil.getUserStockInfo("src/test/resources/user/2016-01-20_15")
    val userStockCodesToday  = FileUtil.getUserStockInfo("src/test/resources/user/2016-01-20_15")


    userStockCodesToday.foreach(x =>{
      val userId = x._1
      //println("userId:",userId)
      if(userStockCodesYesterday.get(userId)== None){
      }else{ // 用户存在

        println("userId:",userId)
        val yesterdayStock = userStockCodesYesterday.get(userId)
        val todayStock = userStockCodesToday.get(userId)

        /*对比新增股票
        *
        */
        var userRateOfReturn = mutable.HashMap[String, RateOfReturn]()
        val todayStockListBuffer = todayStock.get
        val yesterdayStockListBuffer = yesterdayStock.get
        var flag = 0
        val userStock = new UserStock()

        //println("todayStockListBuffer    :",todayStockListBuffer)
        //println("yesterdayStockListBuffer:",yesterdayStockListBuffer)
        //println("++++++++++++++++++++++++")

        todayStockListBuffer.foreach(x=>{
          if(returnOfReturn.contains(x))
            userRateOfReturn.+=((x,returnOfReturn.get(x).get))
        })

        println("userRateOfReturn size",userRateOfReturn.size)

        yesterdayStockListBuffer.foreach(y=>{
          if(userRateOfReturn.contains(y))
            userRateOfReturn = userRateOfReturn - y
          else
            println("New Add Stock Code :",y)
        })

        println("New userRateOfReturn size:",userRateOfReturn.size)
        userStock.setStockes(userRateOfReturn)
        userRate.calculate(userStock)

       // println("user rateOfReturn:",userStock.getCurrentRate())
        userRate.calculate(userStock)
        user.+=(userStock)
        println("************************")

      }
    })


    user.foreach(x =>{
      println("uid:"+x.getUid()+",rate:"+x.getCurrentRate())
      println("=====================")
    })



  }



}
