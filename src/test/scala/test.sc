import scala.collection.mutable
import scala.collection.mutable.ListBuffer

var list = ListBuffer[String]()
list += "string"
list += "hello"
list.size
list.length
val map = mutable.HashMap[String, String]()
map.put("1","2")

for (item <- map) {
  println(item._1)
  println(item._2)
}