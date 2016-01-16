package net

import log.SULogger
import stock.Stock

import scala.collection.mutable
import dispatch._,Defaults._
import scala.util.{Success, Failure}

/**
  * Created by kerry on 16/1/13.
  */
abstract class BaseHttp {

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
    if (strParam != null) {
      if (url.indexOf("?") >= 0)
        strUrl += "&" + strParam
      else
        strUrl += "?" + strParam
    }

    strUrl
  }

  def request(strUrl:String, parameters:mutable.HashMap[String,String], parse: String => Unit): Unit = {

    val finalUrl = getUrl(strUrl, parameters)

    val req = url(finalUrl)
    val response = Http(req OK as.String)

    response onComplete {
      case Success(content) =>
        parse(content)

      case Failure(t) =>
        SULogger.warn("An error has occurred: " + t.getMessage)
    }
  }

  def post(strUrl:String, parameters:mutable.HashMap[String,String], parse: String => Unit): Unit = {

    val post = url(strUrl) << parameters
    val response : Future[String] = Http(post OK as.String)

    response onComplete {
      case Success(content) =>
        parse(content)
      case Failure(t) =>
        SULogger.warn("An error has occurred: " + t.getMessage)
    }
  }
}
