package util

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by yangshuai on 2016/1/25.
  */
class TimeUtilTest extends FlatSpec with Matchers {

  it should "get the string of one pre work day" in {
    val result = TimeUtil.getPreWorkDay(0)
    println(result)
  }

}
