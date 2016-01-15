package analysis

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos
import org.apache.hadoop.hbase.util.Base64

/**
  * Created by C.J.YOU on 2016/1/13.
  */
class HBase {

  private[this] var _tableName: String = new Predef.String

  def tableName: String = _tableName

  def tableName_=(value: String): Unit = {
    _tableName = value
  }

  private[this] var _columnFamliy: String = new Predef.String

  def columnFamliy: String = _columnFamliy

  def columnFamliy_=(value: String): Unit = {
    _columnFamliy = value
  }

  private[this] var _column: String = new Predef.String

  def column: String = _column

  def column_=(value: String): Unit = {
    _column = value
  }
  private  var scan = new Scan()

  private var conf = new Configuration()

  def setScan(scan:Scan): Unit ={
    val proto:ClientProtos.Scan = ProtobufUtil.toScan(scan)
    val scanToString = Base64.encodeBytes(proto.toByteArray)
    conf.set(TableInputFormat.SCAN, scanToString)
    conf
  }
  def getConfigure(table:String,columnFamliy:String,column:String): Configuration = {
    conf = HBaseConfiguration.create()
    conf.set("hbase.rootdir", "hdfs://server:9000/hbase")
    //使用时必须添加这个，否则无法定位
    conf.set("hbase.zookeeper.quorum", "server")
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    conf.set(TableInputFormat.INPUT_TABLE, table)
    conf.set(TableInputFormat.SCAN_COLUMN_FAMILY,columnFamliy)
    conf.set(TableInputFormat.SCAN_COLUMNS,column)
    conf
  }
}
object TestHBase{
  def main(args: Array[String]) {
    val hbase = new HBase
    hbase.tableName=("1")
    hbase.column=("content")
    hbase.columnFamliy=("basic")
    val scan  = new Scan()
    val currentTimeStamp = System.currentTimeMillis()
    scan.setTimeRange(currentTimeStamp - 3600000,currentTimeStamp)
    hbase.getConfigure(hbase.tableName,hbase.columnFamliy,hbase.column)
    hbase.setScan(scan)

  }
}
