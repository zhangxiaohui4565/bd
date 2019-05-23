package com.gupao.bd.sample.flink.realtime.window


import com.gupao.bd.sample.flink.realtime.bean.SensorInfo
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows
import org.apache.flink.streaming.api.windowing.time.Time



/**
  * 会话窗口
  */
object SessionWindow {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

    env.socketTextStream("127.0.0.1", 7777)
      .map(line => {
        val tokens = line.split(",")
        tokens
      })
      .filter(_.length == 3)
      .map(tokens => SensorInfo(tokens(0).trim.toInt, tokens(1).trim.toLong, tokens(2).trim.toLong))
      .keyBy(0)
      .window(ProcessingTimeSessionWindows.withGap(Time.seconds(10)))
      .max(1)
      .print()

    env.execute("word count")
  }
}
