import java.io.File

import scala.sys.process._

/**
  * Created by yangshuai on 2016/1/17.
  */
object UntrackFiles extends App {

  val cmd = "git status"
  val output = cmd.!!
  println(output)

  val folder = new File(".idea/libraries")
  folder.listFiles.foreach(file => {
    val untrackCmd = "git update-index --assume-unchanged " + file.getPath
    val op = untrackCmd.!!
    println(op)
  })
}
