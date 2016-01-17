package analysis

import org.apache.hadoop.hbase.client.Scan
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by C.J.YOU on 2016/1/17.
  */
class HBaseTest extends FlatSpec with Matchers{

  "hbase class method function " should "work" in {
    val ttoh = new TableOneHbase
    ttoh.tableName=("1")
    ttoh.column=("content")
    ttoh.columnFamliy=("basic")

    val scan  = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - 3600000,currentTimeStamp)
    ttoh.getConfigure(ttoh.tableName,ttoh.columnFamliy,ttoh.column)
    ttoh.setScan(scan)
    ttoh.get("rowKey",ttoh.tableName,ttoh.columnFamliy,ttoh.column)

    val htmlSource ="<html info >"
    ttoh.getUserId(htmlSource)
    ttoh.parseDocument(htmlSource)
  }

}
