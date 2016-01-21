package data

import org.scalatest.{Matchers, FlatSpec}

import scala.collection.mutable.ListBuffer

/**
  * Created by yangshuai on 2016/1/21.
  */
class FileUtilTest extends FlatSpec with Matchers {

  it should "" in {

    val list = FileUtil.readFile("E:\\CODE\\JAVA\\pom.xml")
    list.foreach(println)
  }

  it should "print true" in {
    println(FileUtil.mkDir("E:\\test"))
  }

  it should "write file" in {
    val arr = ListBuffer[String]("hehe", "hello", "world")
    FileUtil.createFile("E:\\test.data", arr)
  }
}
