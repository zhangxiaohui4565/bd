package com.gupao.bd.spark_streaming.demo.dstream

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}

/**
  * 功能：从SocketInputDStream消费数据，计算累计WordCount
  **/
object StatefulNetworkWordCount extends StreamingExample {

  val sparkConf = new SparkConf().setMaster("local[4]").setAppName("StatefulNetworkWordCount")
  val ssc = new StreamingContext(sparkConf, Seconds(1))
  ssc.checkpoint("checkpoint")

  // Initial state RDD for mapWithState operation
  val initialRDD = ssc.sparkContext.parallelize(List(("hello", 1), ("world", 1)))

  val lines = ssc.socketTextStream(hostname, port)
  val words = lines.flatMap(_.split(" "))
  val wordDstream = words.map(x => (x, 1))

  // 计算Word的累计数据，通过State保留历史值
  val mappingFunc = (word: String, one: Option[Int], state: State[Int]) => {
    val sum = one.getOrElse(0) + state.getOption.getOrElse(0)
    val output = (word, sum)
    state.update(sum)
    output
  }

  val stateDstream = wordDstream.mapWithState(StateSpec.function(mappingFunc).initialState(initialRDD))
  stateDstream.print()
  ssc.start()
  ssc.awaitTermination()
}
