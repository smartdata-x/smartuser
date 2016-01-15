package calculate

/**
  * Created by C.J.YOU on 2016/1/15.
  */
object TestRateOfReturn {
  def main(args: Array[String]) {
    val id = "115653561"
    val date = "2016-01-15 15"
    val tsr = new RateOfReturn
    val stocklist =tsr.getUserStock(date,id)
    stocklist.foreach(println(_))
  }

}
