package util
import java.util.Calendar
import data.FileUtil
import org.scalatest.{FlatSpec, Matchers}
import stock.Stock

import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/16.
  */
class FileUtilTest extends FlatSpec with Matchers {

  "writeStockObject method" should "work" in {

    val stock_one = new Stock("","sh000001",1,1,1,1,1,1,1,1,1,1,1,1,1,1,11,1,1,1,1,1,1,1,1,1,1,1,1,"t","time")
    val stock_two = new Stock("","sh000002",2,2,1,1,1,1,1,1,1,1,1,1,1,1,11,1,1,1,1,1,1,1,1,1,1,1,1,"t2","time2")
    val list = new mutable.ListBuffer[Stock]
    list.+=(stock_one)
    list.+=(stock_two)
    FileUtil.writeStockList(list)
  }

  "getUserInfo method " should "work" in {

    val hashMap = FileUtil.getUserStockInfo("src/test/resources/user/2016-01-20_15")
    hashMap.foreach(x =>{
      val key = x._1
      val value =x._2
      println("key:"+key)
      print("value:")
      for (list <- value){
        print(list+",")
      }
      println
    })
  }

  it should "get more than 2800 stocks" in {

    val map = FileUtil.readStockCodeByDayAndHour(-1, 13)

    map.size should be > 2800
  }

  it should "get more than 3000 users" in {

    val map = FileUtil.readUserInfoByDayAndHour(-1, 14)

    for ((key, value) <- map) {
      println(key + "<-" + value)
    }

    println(map.size)
  }
}