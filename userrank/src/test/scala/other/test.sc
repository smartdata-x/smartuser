import scala.collection.mutable

val map = mutable.Map[String, Int]()
map.put("1", 11)
map.put("2", 2)
map.put("3", 5)
map.put("4", 7)
map.put("5", 3)

map


for ((key, value) <- map.toSeq.sortBy(_._2).reverse) {
  println(value)
}