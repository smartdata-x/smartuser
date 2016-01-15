package com.kunyandata.other

/**
  * Created by yangshuai on 2016/1/15.
  */
object Test extends App {


  val str = "大秦铁路,7.590,7.620,7.600,7.610,7.570,7.590,7.600,2129700,16165745.000,49200,7.590,116100,7.580,167000,7.570,264700,7.560,143500,7.550,96720,7.600,34100,7.610,58800,7.620,76600,7.630,70100,7.640,2016-01-15,09:39:19,00"
  val arr = str.split(",")
  println(arr.size)
  val str2 = "val name:String,\n           val todayOpeningPrice: Float,\n           val yesterdayClosingPrice: Float,\n           val currentPrice: Float,\n           val todayHighestPrice: Float,\n           val todayLowestPrice: Float,\n           val highestBuyPrice: Float,\n           val lowestSellPrice: Float,\n           val transactionNumber: Long,\n           val transactionMoney: Long,\n           val highestBuyNumber: Long,\n           val secondHighestBuyPrice: Float,\n           val secondHighestBuyNumber: Long,\n           val thirdHighestBuyPrice: Float,\n           val thirdHighestBuyNumber: Long,\n           val fourthHighestBuyPrice: Float,\n           val fourthHighestBuyNumber: Long,\n           val fifthHighestBuyPrice: Float,\n           val fifthHighestBuyNumber: Long,\n           val lowestBuyNumber: Long,\n           val secondLowestBuyPrice: Float,\n           val secondLowestBuyNumber: Long,\n           val thirdLowestBuyPrice: Float,\n           val thirdLowestBuyNumber: Long,\n           val fourthLowestBuyPrice: Float,\n           val fourthLowestBuyNumber: Long,\n           val fifthLowestBuyPrice: Float,\n           val fifthLowestBuyNumber: Long,\n           val date: String,\n           val time: String"
  val arr2 = str2.split(",")
  println(arr2.size)
  for (i <- 0 to arr2.size - 1) {
    println(i)
    println(arr2(i) + ":" + arr(i))
  }

}
