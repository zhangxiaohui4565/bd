package com.gupao.bd.sample.flink.realtime.window


import com.gupao.bd.sample.flink.realtime.bean.SensorInfo
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.{TumblingEventTimeWindows, TumblingProcessingTimeWindows}

/**
  * 翻滚时间窗口
  */
object TumblingTimeWindow {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
//    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val ds = env.socketTextStream("127.0.0.1", 7777)
      .map(line => {
        val tokens = line.split(",")
        tokens
      })
      .filter(_.length == 3)
      .map(tokens => SensorInfo(tokens(0).trim.toInt, tokens(1).trim.toInt, tokens(2).trim.toLong))
//      .assignTimestampsAndWatermarks( new AssignerWithPeriodicWatermarks[CarWc]{
//        var maxTs = Long.MinValue
//        override def getCurrentWatermark: Watermark = {
//           new Watermark(maxTs - 1)
//        }
//        override def extractTimestamp(element: CarWc, previousElementTimestamp: Long): Long = {
//          maxTs = Math.max(element.ts,maxTs)
//          println(element.ts)
//          element.ts
//        }
//      })
      .keyBy(_.sensorId)
      .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
      .sum("speed")

      ds.print()

    env.execute(this.getClass.getSimpleName)
  }
}
