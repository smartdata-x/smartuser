package util

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by yangshuai on 2016/1/27.
  */
class FileUtilTest extends FlatSpec with Matchers {

  it should "get top user list" in {
    FileUtil.readTopUserList.foreach(println)
  }
}
