import org.apache.spark.mllib.classification.{LogisticRegressionWithSGD, NaiveBayes, SVMWithSGD}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by Christiph on 2016/4/1.
  */
  
object TargetClassification {

 /** ********************************************************
  * 1. create training dataset
  * ********************************************************/

  def main(args: Array[String]): Unit = {
  
    val conf = new SparkConf().setAppName("Target")
    val sc = new SparkContext(conf)

    // input total data
    val data1 = sc.textFile("file:///home/cc/targetData/*")
                .map(_.split("\t")).filter(x => x.size == 6)
                .map(x => (x(1), x(2), getUrlU2(x(3)), getUrlU2(x(4))))
                .filter(x => !x._2.contains("spider") && !x._2.contains("Spider"))
    val data = data1.filter(x => x._3 != "qq.com" && x._3 != "sina.com.cn")
    data.cache()

    // input original url
    val urlOriginal = sc.textFile("file:///home/cc/inputnew/stock_original.txt")
                .map(x => x.trim)
    val totalUrl = urlOriginal.map(x => getUrlK(x))
                .distinct().filter(x => x.length > 0)
                .filter(x => !x.contains("163"))
                .collect.mkString(",", ",", ",")
    
    // label the target url
    val labelLineRddUr = data.filter(x => totalUrl.contains(x._3)).map(x => {
      val url = x._3
      val domain = url
      (x._1 + "\t" + domain, 1)
    }).reduceByKey(_ + _)
    labelLineRddUr.cache()
    val urlNumUr = labelLineRddUr.map(_._1.split("\t")).map(x => (x(0), 1)).reduceByKey(_ + _)
    
    // label the target reference
    val labelLineRddRe = data.filter(x => totalUrl.contains(x._4)).map(x => {
      val url = x._4
      val domain = url
      (x._1 + "\t" + domain, 1)
    }).reduceByKey(_ + _)
    labelLineRddRe.cache()
    val urlNumRe = labelLineRddRe.map(_._1.split("\t")).map(x => (x(0), 1)).reduceByKey(_ + _)
    
    // label the AD or IP manually 
    val finalTable = urlNumUr.join(urlNumRe)
    val stockP = finalTable.filter(x => x._1.size < 40).filter(x => x._2._1 >= 10 || x._2._2 >= 10).map(x => (x._1, 1))
    val stockN = finalTable.filter(x => x._1.size < 40).filter(x => x._2._1 == 1 && x._2._2 == 1).map(x => (x._1, 0))
    val stockSample = stockP.union(stockN)
 
    // transform the training data to the standard format and save
    val features = sc.textFile("file:///home/cc/ffff/finalTableL")
                .map(x => x.replace("(", "")).map(x => x.replace(")", ""))
                .map(_.split(",")).filter(x => x(0).contains("."))
                .filter(x => x.size == 5)
                .map(x => (x(0), (x(1).toDouble, x(2).toDouble, x(3).toDouble, x(4).toDouble)))
    val stockTrain1 = stockSample.leftOuterJoin(features).values
    val stockTrain = stockTrain1.map(x =>
      try {
        x._1 + "," + x._2.get._1 + " " + x._2.get._2 + " " + x._2.get._3 + " " + x._2.get._4
      } catch {
        case e: Exception =>
      }
    )
    stockTrain.coalesce(1, true).saveAsTextFile("file:///home/cc/ffff/richAdmodel/trainingdata")
  }


    /** ********************************************************
      * 2. build models and select the best one
      * ********************************************************/

    def main2(args: Array[String]): Unit = {
      val conf = new SparkConf()
        .setAppName("Target")
      val sc = new SparkContext(conf)

    val input = sc.textFile("file:///home/cc/ffff/richIpmodel/trainingdata/p*")
                .filter(x => !x.contains("()"))
    
    val parsedData = input.map { line =>
      val parts = line.split(",")
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(" ").map(_.toDouble)))
    }.cache()

    val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val train = splits(0).cache()
    val test = splits(1).cache

      /** **
        * 2.1 logistic regression model
        * ***/
    val modelLogistic = LogisticRegressionWithSGD.train(train, 50)
    modelLogistic.save(sc, "file:///home/cc/ffff/stockIpmodel/logisticmodel")

    val predictionAndLabels1 = test.map {
      case LabeledPoint(label, features) =>
        val prediction = modelLogistic.predict(features)
        (prediction, label)
    }
    val metrics1 = new MulticlassMetrics(predictionAndLabels1)
    val precision1 = metrics1.precision

    /** **
      * 2.2 SVM regression model
      * ***/
    val modelSVM = SVMWithSGD.train(train, 50)
    modelSVM.save(sc, "file:///home/cc/ffff/stockIpmodel/SVMmodel")

    val predictionAndLabels2 = test.map {
      case LabeledPoint(label, features) =>
        val prediction = modelSVM.predict(features)
        (prediction, label)
    }
    val metrics2 = new MulticlassMetrics(predictionAndLabels2)
    val precision2 = metrics2.precision

    /** ***
      * 2.3 NaiveBayes Model
      * ***/
    val modelBayes = NaiveBayes.train(train)
    modelBayes.save(sc, "file:///home/cc/ffff/stockIpmodel/Bayesmodel")

    val predictionAndLabels3 = test.map {
      case LabeledPoint(label, features) =>
        val prediction = modelBayes.predict(features)
        (prediction, label)
    }
    val metrics3 = new MulticlassMetrics(predictionAndLabels3)
    val precision3 = metrics3.precision


    /** ********************************************************
      * 3. use the best model to do prediction
      * ********************************************************/

    val stock = sc.textFile("file:///home/cc/ffff/finalTableL").map(x => x.replace("(", "")).map(x => x.replace(")", "")).map(_.split(",")).filter(x => x(0).contains(".")).filter(x => x.size == 5).map(x => (x(0), x(1).toDouble, x(2).toDouble, x(3).toDouble, x(4).toDouble))

    val result = stock.map { line =>
      val IP = line._1
      val feature = Vectors.dense(line._2, line._3, line._4, line._5)
      val prediction = modelBayes.predict(feature)
      (IP, feature, prediction)
    }

    val positive = result.filter(x => x._3 == 1.0)
    
    result.coalesce(1, true).saveAsTextFile("file:///home/cc/ffff/richIpmodel/totalResult")
    positive.coalesce(1, true).saveAsTextFile("file:///home/cc/ffff/richIpmodel/positiveResult")

  }
  
  
  def getUrlU1(url: String): String = {
    var part = url
    try {
      if (url.contains("http://")) {
        part = url.replace("http://", "").split("/").filter(x => x.size > 0)(0)
      } else if (url.contains("https://")) {
        part = url.replace("https://", "").split("/").filter(x => x.size > 0)(0)
      } else {
        part = url.split("/").filter(x => x.size > 0)(0)
      }
    } catch {
      case e: Exception =>
    }
    part
  }

  def getUrlU2(url: String): String = {
    val host = getUrlU1(url)
    var part = host
    try {
      if (host.startsWith("www.")) {
        part = host.replace("www.", "")
      } else if (!host.contains("www")) {
        part = host
      }
    } catch {
      case e: Exception =>
    }
    part
  }

  def getUrlK(url: String): String = {
    var part = url
    if (url.contains("http://www")) {
      part = url.substring(11, url.length)
    } else if (url.contains("https://www")) {
      part = url.substring(12, url.length)
    } else {
      part = url
    }
    part
  }
}