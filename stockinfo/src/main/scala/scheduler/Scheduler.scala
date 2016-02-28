package scheduler

import config.FileConfig
import log.SILogger
import net.SinaRequest
import stock.Stock
import util.FileUtil

import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/26.
  */
object Scheduler {

  var stockList = new ListBuffer[Stock]()
  var stockStrList = new ListBuffer[String]()

  def main(args: Array[String]): Unit = {

    while (true) {

      stockList.clear

      SILogger.warn("Task begin.")

      stockStrList = FileUtil.readAllStocks()

      SILogger.warn("Distinct stock number: " + stockStrList.size)

      SinaRequest.requestStockList(stockStrList.toList)

      Timer.waitToNextTask()
    }
  }

}
