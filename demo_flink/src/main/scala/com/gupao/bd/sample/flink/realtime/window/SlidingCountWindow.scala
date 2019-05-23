package com.gupao.bd.sample.flink.realtime.window

import com.gupao.bd.sample.flink.realtime.bean.SensorInfo
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}

/**
  * 滑动计数窗口
  */
object SlidingCountWindow {
  def main(args: Array[String]): Unit = {
    val params = ParameterTool.fromArgs(args)
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.getConfig.setGlobalJobParameters(params)

    env.setParallelism(1)

    env.socketTextStream("127.0.0.1", 7777)
      .map(line => {
        val tokens = line.split(",")
        SensorInfo(tokens(0).trim.toInt, tokens(1).trim.toLong)
      })
      .keyBy(0)
      .countWindow(5, 3)
      .sum(1)
      .print()

    env.execute(getClass.getSimpleName)
  }
}
