package net

import dispatch._, Defaults._
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success}


/**
  * Created by yangshuai on 2016/1/15.
  */
class SinaRequestTest extends FlatSpec with Matchers {

  it should "" in {

    val svc = url("http://hq.sinajs.cn/?list=sh601006")
    val response : Future[String] = Http(svc OK as.String)

    response onComplete {
      case Success(content) => {
        println("success")
        val result = response.value.get.get
        val stock = SinaRequest.parse(result)
      }

      case Failure(t) => {
        println("fail")
      }
    }
  }
}
