package scheduler

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

  def main(args: Array[String]): Unit = {

    while (true) {

      SILogger.warn("Task begin.")

      val set = FileUtil.readLatestStocks()

      SILogger.warn("Distinct stock number: " + set.size)

      SinaRequest.requestStockList(set.toList)

      Timer.waitToNextTask()
    }
  }

}
