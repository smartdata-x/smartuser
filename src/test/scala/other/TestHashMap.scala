/**
  * Created by kerry on 16/1/19.
  */
object TestHashMap {
  var sideFX = 0.0
  def nonPure(x:Double) = {
    //sideFX += x
    x
  }

  def main(args: Array[String]) {
    val  myMap = Map("a"->1.0, "b" -> 2.0, "c"-> 3.0)
    val mapValues = myMap.mapValues(nonPure)

    println("sum1 = %.2f".format(mapValues.values.sum))
    //println("sideFX = %.2f".format(sideFX))
    /*
    println("sum2 = %.2f".format(mapValues.values.sum))
    println("sum3 = %.2f".format(mapValues.values.sum))
    */
  }
}
