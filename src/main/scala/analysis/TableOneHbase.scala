package analysis

import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Result, Scan}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import java.util.regex.Pattern
import scala.collection.mutable

/**
  * Created by C.J.YOU on 2016/1/15.
  */
class TableOneHbase extends HBase{

  def get(rowKey:String,table:String,columnFamliy:String,column:String):Result = {
    val connection = ConnectionFactory.createConnection(HBaseConfiguration.create())
    val htable = connection.getTable(TableName.valueOf("1"))
    val get = new Get(Bytes.toBytes(rowKey))
    val resultOfGet = htable.get(get)
    resultOfGet
  }

  def parseDocument(sc:String):mutable.MutableList[String] ={
    val followStockCodeList = new mutable.MutableList[String]
    val pattern = Pattern.compile("\"\\d{6}\"")
    val m = pattern.matcher(sc)
    if(m != null){
      while (m.find()) {
        val n = m.groupCount()
        for (i <- 0 to n ) {
          val outputValue = m.group(i)
          if (outputValue != null) {
            followStockCodeList.+=(outputValue.substring(1,7))
          }
        }
      }
    }
    followStockCodeList
  }
  def getUserId(sc:String):String = {
    var userId = new String
    val pattern = Pattern.compile("\\[\'userid\'\\]\\s*=\\s*\'\\d{1,}\'")
    val m = pattern.matcher(sc)
    if(m != null){
      if(m.find()) {
        val outputValue = m.group(0)
        if (outputValue != null) {
          val patternId = Pattern.compile("\\d{1,}")
          val mId = patternId.matcher(outputValue)
          if(mId.find()) {
            val outputValueId = mId.group(0)
            userId = outputValueId
          }
        }
      }
    }
    userId
  }
}
