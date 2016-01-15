package net

import dispatch._, Defaults._
import org.scalatest.{Matchers, FlatSpec}
import stock.Stock

import scala.util.{Failure, Success}

/**
  * Created by yangshuai on 2016/1/15.
  */
class TencentRequestTest extends FlatSpec with Matchers {

  it should "" in {

    val svc = url("http://qt.gtimg.cn/?q=sh601006")
    val response : Future[String] = Http(svc OK as.String)

    var stock:Stock = null
    var stock2:Stock = null
    var flag = false
    var flag2 = false


    response onComplete {
      case Success(content) => {
        flag = true
        println("success")
        val result = response.value.get.get
        stock = TencentRequest.parse(result)
      }

      case Failure(t) => {
        println("fail")
      }
    }

    val svc2 = url("http://hq.sinajs.cn/?list=sh601006")
    val response2 : Future[String] = Http(svc2 OK as.String)

    response2 onComplete {
      case Success(content) => {
        flag2 = true
        println("success")
        val result = response2.value.get.get
        stock2 = SinaRequest.parse(result)

        if (flag && flag2) {
          println("enter")
          println(stock.name + ":" + stock2.name)
          println(stock.todayOpeningPrice + ":" + stock2.todayOpeningPrice)
          println(stock.yesterdayClosingPrice + ":" + stock2.yesterdayClosingPrice)
          println(stock.currentPrice + ":" + stock2.currentPrice)
          println(stock.todayHighestPrice + ":" + stock2.todayHighestPrice)
          println(stock.todayLowestPrice + ":" + stock2.todayLowestPrice)
          println(stock.transactionNumber + ":" + stock2.transactionNumber)
          println(stock.transactionMoney + ":" + stock2.transactionMoney)
          println(stock.highestBuyNumber + ":" + stock2.highestBuyNumber)
          println(stock.highestBuyPrice + ":" + stock2.highestBuyPrice)
          println(stock.secondHighestBuyNumber + ":" + stock2.secondHighestBuyNumber)
          println(stock.secondHighestBuyPrice + ":" + stock2.secondHighestBuyPrice)
          println(stock.thirdHighestBuyNumber + ":" + stock2.thirdHighestBuyNumber)
          println(stock.thirdHighestBuyPrice + ":" + stock2.thirdHighestBuyPrice)
          println(stock.fourthHighestBuyNumber + ":" + stock2.fourthHighestBuyNumber)
          println(stock.fourthHighestBuyPrice + ":" + stock2.fourthHighestBuyPrice)
          println(stock.fifthHighestBuyNumber + ":" + stock2.fifthHighestBuyNumber)
          println(stock.fifthHighestBuyPrice + ":" + stock2.fifthHighestBuyPrice)
          println(stock.lowestBuyNumber + ":" + stock2.lowestBuyNumber)
          println(stock.lowestSellPrice + ":" + stock2.lowestSellPrice)
          println(stock.secondLowestBuyNumber + ":" + stock2.secondLowestBuyNumber)
          println(stock.secondLowestBuyPrice + ":" + stock2.secondLowestBuyPrice)
          println(stock.thirdLowestBuyNumber + ":" + stock2.thirdLowestBuyNumber)
          println(stock.thirdLowestBuyPrice + ":" + stock2.thirdLowestBuyPrice)
          println(stock.fourthLowestBuyNumber + ":" + stock2.fourthLowestBuyNumber)
          println(stock.fourthLowestBuyPrice + ":" + stock2.fourthLowestBuyPrice)
          println(stock.fifthLowestBuyNumber + ":" + stock2.fifthLowestBuyNumber)
          println(stock.fifthLowestBuyPrice + ":" + stock2.fifthLowestBuyPrice)
          println(stock.date + ":" + stock2.date)
          println(stock.time + ":" + stock2.time)
        }
      }

      case Failure(t) => {
        println("fail")
      }
    }

    println("hello")


  }

}
