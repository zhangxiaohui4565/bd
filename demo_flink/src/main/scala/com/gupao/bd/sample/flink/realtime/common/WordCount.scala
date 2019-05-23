package com.gupao.bd.sample.flink.realtime.common

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala._


/**
  * nc -l 7777
  * hello world
  * hello flink
  */
object WordCount {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val ds = env.socketTextStream("127.0.0.1", 7777)
      .flatMap(line => line.split("\\s+").map(word => (word, 1)))
      .keyBy(0)
      .timeWindow(Time.seconds(10))
      .sum(1)

    println(env.getExecutionPlan)
    env.execute(getClass.getSimpleName)

  }
}
