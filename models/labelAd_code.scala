import org.apache.spark.{SparkContext, SparkConf}
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks

/**
  * Created by Wangcao on 2016/3/22.
  * 
  * 1.为每个用户ad的上网行为（点击的url）打上标签，以区别奢侈品url,或者股票url
  * 2.统计分析每个用户上奢侈品网站与股票网站的行为
  * 3.根据点击次数，url个数，占比等维度筛选与分类有钱人与少钱人，玩股票的与不玩股票的人。
  */
  
object TargetMan {

  def main(args:Array[String]): Unit ={

    val conf = new SparkConf()
      .setAppName("Richman")
      .setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    val data = sc.textFile("file:///home/telecom/lcm20160313/20160310/datelabel=20160310/loadstamp=*")
    val url_oo = sc.textFile("file:/home/cc/stock_original.txt")
    var url_ee = sc.textFile("file:/home/cc/stock_extra.txt")

    val url_o = url_oo.map(x=>x.trim)
    val url_e = url_ee.map(_.split(",")).filter(x=>x.size==2).map(parts=>parts(0))
    val url_Total = url_o.union(url_e).distinct().filter(x=>x.size>0).take(1000)
    var key = new ArrayBuffer[String]()
    for (i <- url_Total) {
      if (i.contains("http://www")) {
        key ++= Array(i.substring(11,i.length))
      } else if(i.contains("https://www")){
        key ++= Array(i.substring(12,i.length))
      }
      else {
        key ++= Array(i)
       }
      }

    val url_key = key.toArray
    val targetBroadcast = sc.broadcast(url_key)
	
    val filterFunc = (x:Array[String])=>{
    val urlValue = x(2)
    val arr = targetBroadcast.value
    var lableTmp = 0
    val loop =new Breaks
      loop.breakable{
        for (tmpval <- arr) {
          if (urlValue.contains(tmpval)){
            lableTmp = 1
            loop.break()
          }
        }
      }
    (x(0),x(2),lableTmp)
    }
	
    val lableline = data.map(x => x.split("\u0001")).filter(x => x.size == 4).map(filterFunc)				  
    val lablelineRdd = lableline.filter(x=>x._2.length>=30).map(x=>Triple(x._1,x._2.substring(0,30),x._3))						  
    //val a = lablelineRdd.map(x => (x(0) + "_" + x(1) , x(2))).reduceByKey(_ + _)

    case class cc(sessionid: String,url: String,label: Integer)
    val lines = lablelineRdd.map(p=>cc(p._1,p._2,p._3))
    val wc=lines.toDF()
    wc.registerTempTable("wc")
    sqlContext.cacheTable("wc")
    wc.show

    val c1 = sqlContext.sql("select sessionid,count(url) as luxury_visit, count(distinct url) as luxury_url from wc where label = 1 group by sessionid order by luxury_visit desc")
    c1.registerTempTable("c1")

    val c2 = sqlContext.sql("select sessionid,count(url) as total_visit,count(distinct url) as total_url from wc  group by sessionid order by total_visit desc")
    c2.registerTempTable("c2")

    val c3 = sqlContext.sql("select a.sessionid,a.luxury_visit,b.total_visit,a.luxury_visit/b.total_visit as visit_rate,a.luxury_url,b.total_url,a.luxury_url/b.total_url as url_rate from c1 a join c2 b on a.sessionid=b.sessionid order by a.luxury_visit desc")
    c3.registerTempTable("c3")

    sqlContext.uncacheTable("c3")
    c3.count()
    c3.describe().show
    c3.where($"luxury_visit">** && $"visit_rate">** && $"luxury_url">** && $"url_rate">**).save("file:///home/cc/")

    target.sort("url_rate").limit(10000).save(file:///home/cc")
  }
}