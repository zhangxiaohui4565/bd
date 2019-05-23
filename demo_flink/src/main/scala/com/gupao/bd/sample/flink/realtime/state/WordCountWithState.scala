package com.gupao.bd.sample.flink.realtime.state


import com.gupao.bd.sample.flink.realtime.bean.CountWithTs
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.util.Collector
import org.apache.flink.streaming.api.scala._



object WordCountWithState {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.createLocalEnvironment()
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

    env.socketTextStream("127.0.0.1", 7777)
      .flatMap(line => line.split(" "))
      .map(word => (word,""))
      .keyBy(_._1)
      .process(new CountWithStateFunction())
      .print()

    env.execute("word count")
  }


  /**
    * ProcessFunction的实现，用来维护计数
    */
  class CountWithStateFunction() extends KeyedProcessFunction[String,(String, String),(String,Long)] {

    private var state: ValueState[CountWithTs] = _

    override def open(parameters: Configuration): Unit = {
      state = getRuntimeContext.
        getState(new ValueStateDescriptor[CountWithTs]("word_count_sum_state", classOf[CountWithTs]))
    }

    override def processElement(value: (String, String),
                                ctx: KeyedProcessFunction[String, (String, String), (String, Long)]#Context,
                                out: Collector[(String, Long)]): Unit ={

      val currentTs = System.currentTimeMillis()
      val current:CountWithTs = state.value() match {
        case null => {
          CountWithTs(value._1, 1, currentTs)
        }
        case CountWithTs(key, count, lastModified) =>
          CountWithTs(key, count + 1, currentTs)
      }

      state.update(current)
      out.collect(current.key,current.count)
    }
  }

}


