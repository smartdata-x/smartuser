package net

import dispatch._, Defaults._
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success}


/**
  * Created by yangshuai on 2016/1/15.
  */
class SinaRequestTest extends FlatSpec with Matchers {

  it should "" in {

    val raw = "var hq_str_sh601006=\"大秦铁路,7.590,7.620,7.350,7.610,7.320,7.340,7.350,54640003,407693882.000,156100,7.340,322085,7.330,313189,7.320,257700,7.310,594600,7.300,264400,7.350,39800,7.360,84411,7.370,246840,7.380,75534,7.390,2016-01-15,14:32:50,00\";"

    val pattern = "(?<==\").*(?=\")".r

    val source = pattern.findFirstIn(raw).get

    val stock = SinaRequest.parse(source)

    println(stock)
  }
}
