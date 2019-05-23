package com.gupao.bd.spark_streaming.demo.dstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * 功能：从HDFS消费数据
  **/
object HdfsWordCount extends StreamingExample {

  val sparkConf = new SparkConf().setAppName("HdfsWordCount")
  val ssc = new StreamingContext(sparkConf, Seconds(2))

  val lines = ssc.textFileStream(inputDir)
  val words = lines.flatMap(_.split(" "))
  val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
  wordCounts.print()

  ssc.start()
  ssc.awaitTermination()

}
