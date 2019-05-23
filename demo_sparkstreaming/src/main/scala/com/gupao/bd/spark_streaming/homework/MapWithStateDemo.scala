package com.gupao.bd.spark_streaming.homework

import com.gupao.bd.spark_streaming.demo.dstream.StreamingExample
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}

/**
  *
  * MapWithState
  *
  * nc -lk 9999
  *
  **/
object MapWithStateDemo extends StreamingExample {

  val sparkConf = new SparkConf().setMaster("local[2]").setAppName("MapWithStateDemo")

  val ssc = new StreamingContext(sparkConf, Seconds(5))

  ssc.checkpoint(checkpointDir)

  ssc.sparkContext.setLogLevel("WARN")

  val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)

  val words = lines.flatMap(_.split(" "))

  val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)

  val mappingFunc = (word: String, curr: Option[Int], preState: State[Int]) => {
    val sum = curr.getOrElse(0) + preState.getOption.getOrElse(0)
    preState.update(sum)
    (word, sum)
  }

  val stateSpec = StateSpec.function(mappingFunc).numPartitions(2).timeout(Seconds(30))

  wordCounts.mapWithState(stateSpec).print()

  ssc.start()
  ssc.awaitTermination()
}
