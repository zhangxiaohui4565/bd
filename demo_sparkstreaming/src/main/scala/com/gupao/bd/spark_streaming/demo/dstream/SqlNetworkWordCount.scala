package com.gupao.bd.spark_streaming.demo.dstream

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}

/**
  * 功能：Streaming程序中通过Spark SQL计算WordCount
  **/
object SqlNetworkWordCount extends StreamingExample {

  val sparkConf = new SparkConf().setMaster("local[3]").setAppName("SqlNetworkWordCount")
  val ssc = new StreamingContext(sparkConf, Seconds(2))
  val lines = ssc.socketTextStream(hostname, port, StorageLevel.MEMORY_AND_DISK_SER)
  val words = lines.flatMap(_.split(" "))

  // Convert RDDs of the words DStream to DataFrame and run SQL query
  words.foreachRDD { (rdd: RDD[String], time: Time) =>

    // 获取SparkSession
    val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)

    import spark.implicits._

    // RDD[String] -> RDD[case class] -> DataFrame
    val wordsDataFrame = rdd.map(w => Record(w)).toDF()

    // 创建临时视图：words
    wordsDataFrame.createOrReplaceTempView("words")

    // 执行SQL
    val wordCountsDataFrame = spark.sql("select word, count(*) as total from words group by word")

    // 打印执行结果
    println(s"========= $time =========")
    wordCountsDataFrame.show()
  }

  ssc.start()
  ssc.awaitTermination()
}

case class Record(word: String)

object SparkSessionSingleton {

  @transient private var instance: SparkSession = _

  def getInstance(sparkConf: SparkConf): SparkSession = {
    if (instance == null) {
      instance = SparkSession
        .builder
        .config(sparkConf)
        .getOrCreate()
    }
    instance
  }
}
