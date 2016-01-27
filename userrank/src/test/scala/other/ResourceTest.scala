package other

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

import org.scalatest.{FlatSpec, Matchers}
import org.w3c.dom.Element
import scheduler.Scheduler

/**
  * Created by yangshuai on 2016/1/27.
  */
class ResourceTest extends FlatSpec with Matchers {

  it should "" in {
    val url = getClass.getResource("/config.xml")
    val file = url.getFile
    val content = url.getContent()

    println(url.getClass.getName)
    println(file)
    println(content)

    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
    val result = document.getElementsByTagName("redis").item(0).asInstanceOf[Element].getElementsByTagName("ip").item(0).getTextContent
    println(result)


  }

}
