package net

import log.SULogger
import stock.Stock

import scala.collection.mutable
import dispatch._,Defaults._
import scala.util.{Success, Failure}

/**
  * Created by kerry on 16/1/13.
  */
abstract class BaseHttp() {

  def parse(response: String): Stock

  def getParameters(parameter: mutable.HashMap[String,String]): String = {
    var strParam:String = ""
    val iterator = parameter.keySet.iterator
    while(iterator.hasNext) {
      val key = iterator.next()
      if(parameter.get(key) != null){
        strParam += key + "=" + parameter.get(key).get
        if(iterator.hasNext)
          strParam += "&"
      }
    }

    strParam
  }

  def getUrl(url:String, parameters:mutable.HashMap[String,String]): String ={
    val strParam = getParameters(parameters)
    var strUrl = url
    println(strParam.getClass.getName)
    if (strParam != null) {
      if (url.indexOf("?") >= 0)
        strUrl += "&" + strParam
      else
        strUrl += "?" + strParam
    }

    strUrl
  }

  def request(strUrl:String, parameters:mutable.HashMap[String,String]): String = {

    val finalUrl = getUrl(strUrl, parameters)

    val req = url(finalUrl)
    val response : Future[String] = Http(req OK as.String)

    response onComplete {
      case Success(content) =>
        return response.value.get.get

      case Failure(t) =>
        SULogger.warn("An error has occurred: " + t.getMessage)
    }

    null
  }

  def post(strUrl:String, parameters:mutable.HashMap[String,String]): String = {

    val finalUrl = getUrl(strUrl, parameters)

    val post = url(finalUrl).POST
    val response : Future[String] = Http(post OK as.String)

    response onComplete {
      case Success(content) =>
        return response.value.get.get

      case Failure(t) =>
        SULogger.warn("An error has occurred: " + t.getMessage)
    }

    null
  }
}


object TestBaseApiRequest{
  def main(args: Array[String]) {
    /*val test = new BaseApiRequest()
    val strParam = new mutable.HashMap[String,String]()
    strParam.put("uid","1")
    strParam.put("token","3213sewqesadas")
    strParam.put("rtype","1")
    strParam.put("stype","2")

    var url = "http://api.prism.smartdata-x.com/cgi-bin/northsea/prsim/stock/1/top_twenty_stock.fcgi"
    test.getRequest(url,strParam)*/
  }
}
