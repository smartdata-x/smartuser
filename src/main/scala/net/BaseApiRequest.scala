package net

import stock.Stock

import scala.collection.immutable.Queue
import scala.collection.mutable
import scala.collection.mutable.HashMap
import dispatch._,Defaults._
import scala.util.{Success, Failure}
import org.apache.log4j.Logger

/**
  * Created by kerry on 16/1/13.
  */
abstract class BaseApiRequest() {

  //private var mQueue = new Queue[String]()
  private  var mResponse:String = ""
  private  val mLog:Logger = Logger.getRootLogger

  def parse(response: String): Stock

  def getParameters(parameter: HashMap[String,String]): String = {
    var strParam:String = ""
    val iter = parameter.keySet.iterator
    while(iter.hasNext) {
      val key = iter.next()
      if(parameter.get(key) != null){
        strParam += key + "=" + parameter.get(key).get
        if(iter.hasNext)
          strParam += "&"
      }
    }
    return strParam
  }

  def getRequest(url:String,requestParameter:HashMap[String,String]): Unit ={
    val strParam = getParameters(requestParameter)
    var strUrl = url
    println(strParam.getClass.getName)
    if (strParam != null) {
      if (url.indexOf("?") >= 0)
        strUrl += "&" + strParam
      else
        strUrl += "?" + strParam
    }

    request(1,strUrl,requestParameter)
  }

  def request(method: Int,strUrl:String,request:mutable.HashMap[String,String]): Stock = {
    val svc = url(strUrl)
    val response : Future[String] = Http(svc OK as.String)
    response onComplete {
      case Success(content) => {
        parse(response.value.get.get)
      }

      case Failure(t) => {
        mLog.warn("An error has occurred: " + t.getMessage)
        null
      }
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
