def callback(): Unit = {
  println("hello world")
}

def execute(func:() => Unit): Unit = {
  func()
}

execute(callback)

try {
  null.toString

} catch {
  case e: Exception =>
    e.printStackTrace
}


