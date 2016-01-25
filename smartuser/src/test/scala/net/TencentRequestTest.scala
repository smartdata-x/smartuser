package net

import dispatch._, Defaults._
import org.scalatest.{Matchers, FlatSpec}
import stock.Stock

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
  * Created by yangshuai on 2016/1/15.
  */
class TencentRequestTest extends FlatSpec with Matchers {

  it should "" in {
    TencentRequest.sendRequest(mutable.HashMap("q" -> "sh601006"))
  }
}
