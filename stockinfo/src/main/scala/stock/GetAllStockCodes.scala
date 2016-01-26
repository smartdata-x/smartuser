package stock

import scala.io.Source

/**
  * Created by yangshuai on 2016/1/26.
  */
object GetAllStockCodes extends App {

  val json = Source.fromURL("http://www.iwencai.com/stockpick/cache?token=4cbdc9457952533c89837357b3933fbf&p=1&perpage=4000&sort={%22column%22:4,%22order%22:%22DESC%22}&showType=[%22%22,%22%22,%22onTable%22,%22onTable%22,%22onTable%22,%22onTable%22").mkString
  println(json)

}
