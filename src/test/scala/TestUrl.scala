import java.io.{BufferedWriter, FileWriter, File}

import dispatch._,Defaults._
import log.SULogger
import org.apache.commons.io.FileUtils

import scala.util.{Failure, Success}

/**
  * Created by yangshuai on 2016/1/17.
  */
object TestUrl extends App {

  val MAX_CODE_NUMBER = 800.0

  val source = new File(getClass.getResource("codes").getFile)
  val fileContent = FileUtils.readFileToString(source)
  val arr = fileContent.trim.split("\n")
  var finalUrl = "http://hq.sinajs.cn/list="
  var i = 0

  val path = "E:\\CODE\\Workspace\\smartuser\\src\\test\\resources\\result"
  FileUtils.deleteQuietly(new File(path))
  val file = new File(path)
  file.createNewFile
  val fileWriter = new FileWriter(path, true)
  val bufferWriter = new BufferedWriter(fileWriter)

  var requestNum = Math.ceil(arr.size / MAX_CODE_NUMBER)

  while (i < arr.length) {
    val head = arr(i).charAt(0)
    if (head == '0' || head == '3') {
      finalUrl += "sz" + arr(i) + ","
    } else if (head == '6' || head == '9') {
      finalUrl += "sh" + arr(i) + ","
    }

    if ((i > 0 && i % MAX_CODE_NUMBER == 0) || i == arr.length - 1) {
      println(finalUrl)
      send(finalUrl)
      finalUrl = "http://hq.sinajs.cn/list="
    }
    i += 1
  }

  println(arr.length)

  def send(finalUrl: String): Unit = {

    val req = url(finalUrl)
    val response = Http(req OK as.String)

    response onComplete {
      case Success(content) =>
        bufferWriter.write(content)
        requestNum -= 1
        if (requestNum == 0) {
          bufferWriter.close()
          Http.shutdown
        }

      case Failure(t) =>
        SULogger.warn("An error has occurred: " + t.getMessage)
    }
  }
}
