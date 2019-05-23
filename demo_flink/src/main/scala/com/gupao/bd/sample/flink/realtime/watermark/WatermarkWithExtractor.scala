package com.gupao.bd.sample.flink.realtime.watermark

import com.gupao.bd.sample.flink.realtime.bean.SensorInfo
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.evictors.CountEvictor
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
  * 水位测试
  */
object WatermarkWithExtractor {
  def main(args: Array[String]): Unit = {

    val maxDelay = 10
    val allowedLateness = 20
    val windowSize = 50

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    env.socketTextStream("127.0.0.1", 7777)
      .assignTimestampsAndWatermarks(new TimestampExtractor(maxDelay))
      .map(line => {
        val tokens = line.split(",")
        tokens
      })
      .filter(_.length == 3)
      .map(tokens => SensorInfo(tokens(0).trim.toInt, tokens(1).trim.toInt, tokens(2).trim.toLong))
      .keyBy(_.sensorId.toString)
      .window(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
      .trigger(CountTrigger.of(5))
      .evictor(CountEvictor.of(2,true))
      .allowedLateness(Time.milliseconds(allowedLateness))
      .apply(new WindowFunctionTest)
      .print()

    env.execute(this.getClass.getSimpleName)
  }

  class WindowFunctionTest extends WindowFunction[SensorInfo, (String, Int, Long, Long, Long, Long), String, TimeWindow] {
    override def apply(key: String, window: TimeWindow,
                       input: Iterable[SensorInfo],
                       out: Collector[(String, Int, Long, Long, Long, Long)]): Unit = {
      val list = input.toList.sortBy(_.ts)
      out.collect(key, input.size, list.head.ts, list.last.ts, window.getStart, window.getEnd)
    }
  }


  //基于water发射器发送watermark
  class TimestampExtractor(maxDelay: Int) extends AssignerWithPeriodicWatermarks[String] with Serializable {
    var maxTs = 0L

    override def extractTimestamp(e: String, prevElementTimestamp: Long) = {
      val ts = e.split(",")(2).toLong
      maxTs = Math.max(ts, maxTs)
      ts
    }

    override def getCurrentWatermark(): Watermark = {
      new Watermark(maxTs - maxDelay)
    }

  }


}





