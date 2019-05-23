package com.gupao.bd.spark_streaming.homework

import com.gupao.bd.spark_streaming.demo.dstream.StreamingExample
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * UpdateStateByKey
  *
  * nc -lk 9999
  *
  **/
object UpdateStateByKeyDemo extends StreamingExample {

  val sparkConf = new SparkConf().setMaster("local[2]").setAppName("UpdateStateByKeyDemo")

  val ssc = new StreamingContext(sparkConf, Seconds(5))

  ssc.checkpoint(checkpointDir)

  ssc.sparkContext.setLogLevel("WARN")

  val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)

  val words = lines.flatMap(_.split(" "))

  val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)

  val updateFunc = (currValues: Seq[Int], prevValueState: Option[Int]) => {
    Some(currValues.sum + prevValueState.getOrElse(0))
  }

  wordCounts.updateStateByKey(updateFunc).print()

  ssc.start()

  ssc.awaitTermination()
}
