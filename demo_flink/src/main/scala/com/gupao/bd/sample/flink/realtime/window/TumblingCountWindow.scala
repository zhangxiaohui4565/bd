package com.gupao.bd.sample.flink.realtime.window

import com.gupao.bd.sample.flink.realtime.bean.SensorInfo
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}

/**
  * 翻滚计数窗口
  */
object TumblingCountWindow {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.socketTextStream("127.0.0.1", 7777)
      .map(line => {
        val tokens = line.split(",")
        tokens
      })
      .filter(_.length == 2)
      .map(tokens => SensorInfo(tokens(0).trim.toInt, tokens(1).trim.toInt))
      .keyBy(0)
      .countWindow(3)
      .sum(1)
      .print()

    env.execute(this.getClass.getSimpleName)
  }



}
