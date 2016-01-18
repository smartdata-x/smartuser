def callback(): Unit = {
  println("hello world")
}

def execute(func:() => Unit): Unit = {
  func()
}

execute(callback)


