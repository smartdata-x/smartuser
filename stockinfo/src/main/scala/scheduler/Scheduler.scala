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

  def main(args: Array[String]): Unit = {

    if (args.length > 0) {
      FileConfig.ROOT_DIR = "E:" + FileConfig.ROOT_DIR
    }

    while (true) {

      stockList.clear

      SILogger.warn("Task begin.")

      val stockCodes = FileUtil.readAllStocks()

      SILogger.warn("Distinct stock number: " + stockCodes.size)

      SinaRequest.requestStockList(stockCodes.toList)

      Timer.waitToNextTask()
    }
  }

}
