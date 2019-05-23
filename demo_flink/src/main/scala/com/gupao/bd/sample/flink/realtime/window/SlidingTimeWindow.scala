package com.gupao.bd.sample.flink.realtime.window

import com.gupao.bd.sample.flink.realtime.bean.SensorInfo
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, SlidingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.evictors.CountEvictor
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger



/**
  * 滑动时间窗口
  */
object SlidingTimeWindow {

  def main(args: Array[String]): Unit = {
    val params = ParameterTool.fromArgs(args)
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(params)
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    env.setParallelism(1)

   val ds = env.socketTextStream("127.0.0.1", 7777)
         .map(r => {
            val rs = r.split(",")
           SensorInfo(rs(0).toInt,rs(1).toLong)
         })

      ds.keyBy(0)
      .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
      .aggregate(new AverageAggregate)
      .print()

    env.execute(getClass.getSimpleName)
  }


  class AverageAggregate extends AggregateFunction[SensorInfo, (Long, Long), Double] {
    override def createAccumulator() = (0L, 0L)

    override def add(value: SensorInfo, accumulator: (Long, Long)) =
      (accumulator._1 + value.speed, accumulator._2 + 1L)

    override def getResult(accumulator: (Long, Long)) = accumulator._1 / accumulator._2

    override def merge(a: (Long, Long), b: (Long, Long)) =
      (a._1 + b._1, a._2 + b._2)
  }


}




