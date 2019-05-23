package com.gupao.bd.sample.flink.realtime.checkpoint


import java.time.LocalDateTime
import java.util.Properties

import com.gupao.bd.sample.flink.realtime.bean.CheckPointType
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.flink.util.Collector

object KafkaCheckpointStream {



  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      println("Usage: plase input kafkaTopic")
      System.exit(0)
    }

    val checkpointInterval = 1000
    val checkpointMode = CheckpointingMode.EXACTLY_ONCE

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.enableCheckpointing(checkpointInterval, checkpointMode)
    env.setStateBackend(new FsStateBackend("file:///tmp/checkpoints"))
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val props = new Properties()
    props.setProperty("bootstrap.servers", "localhost:9092")
    props.put("auto.offset.reset", "latest")
    props.setProperty("group.id", "flink-kafka-test")
    props.put("enable.auto.commit", "false")

    env.setParallelism(2)

    val consumer = new FlinkKafkaConsumer011[String](args(0), new SimpleStringSchema(), props)
    env
      .addSource(consumer)
      .map(k => {
        val tokens = k.split(",")
        CheckPointType(tokens(0), tokens(1).toLong, tokens(3).toDouble)
      })
      .assignAscendingTimestamps(_.ts)
      .keyBy(_.key)
      .flatMap(new StatefuelMapper)
      .print()

    env.execute(getClass.getSimpleName)
  }

  class StatefuelMapper extends RichFlatMapFunction[CheckPointType, String] {
    private var state: ValueState[Integer] = _

    override def flatMap(value: CheckPointType, collector: Collector[String]): Unit = {
      var currentState = state.value()
      if (currentState == null) {
        currentState = 0
      }
      currentState += 1

      collector.collect(String.format("%s: (%s,%d)", LocalDateTime.now(), value, currentState))
      state.update(currentState)
    }

    override def open(parameters: Configuration): Unit = {
      state = getRuntimeContext.
        getState(new ValueStateDescriptor[Integer]("checkpoint_example", classOf[Integer]))
    }
  }

}

