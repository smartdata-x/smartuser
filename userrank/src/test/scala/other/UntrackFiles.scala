package other


import scala.sys.process._

/**
  * Created by yangshuai on 2016/1/17.
  * 慎用!!!
  */
object UntrackFiles extends App {

  val cmd = "git status"
  val output = cmd.!!
  val arr = output.split("\n")
  for (i <- 6 until arr.size - 2) {
    val untrackCmd = "git update-index --assume-unchanged " + arr(i).substring(12)
    val op = untrackCmd.!!
    println(arr(i))
  }
}
