package util

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by yangshuai on 2016/1/26.
  */
class FileUtilTest extends FlatSpec with Matchers {

  it should "" in {
    val set = FileUtil.readLatestStocks()
    println(set.size)
    for (stock <- set) {
      println(stock)
    }
  }

}
