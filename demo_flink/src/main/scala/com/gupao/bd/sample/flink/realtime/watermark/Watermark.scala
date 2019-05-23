package com.gupao.bd.sample.flink.realtime.watermark

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows
import org.apache.flink.streaming.api.windowing.time.Time


object Watermark {
  def main(args: Array[String]): Unit = {

    val params = ParameterTool.fromArgs(args)
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.getConfig.setGlobalJobParameters(params)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(1)

    val input = List(
      ("a", 1L, 1),
      ("b", 1L, 1),
      ("b", 3L, 2),
      ("b", 5L, 1),
      ("c", 6L, 1),
      ("a", 10L, 1),
      ("c", 11L, 1)
    )

    //基于source端发送watermark
    val source = env.addSource( new SourceFunction[(String,Long,Int)](){
      override def run(ctx: SourceFunction.SourceContext[(String, Long, Int)]): Unit = {
        input.foreach(value =>{
          ctx.collectWithTimestamp(value,value._2)
          ctx.emitWatermark(new Watermark(value._2 -1))
        })
        ctx.emitWatermark(new Watermark(Long.MaxValue))
      }
      override def cancel(): Unit = {

      }
    })


    val agg = source
      .keyBy(0)
      .window(EventTimeSessionWindows.withGap(Time.milliseconds(3L)))
      .sum(2)


      agg.print()

      env.execute()
  }

}
