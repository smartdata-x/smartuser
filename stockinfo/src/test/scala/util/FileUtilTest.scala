package util

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by yangshuai on 2016/1/26.
  */
class FileUtilTest extends FlatSpec with Matchers {

  it should "get more than 2800 stock codes" in {

    val list = FileUtil.readAllStocks()
    list.size should be > 2800
  }

}
