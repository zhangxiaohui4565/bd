package com.gupao.bd.sample.flink.realtime.sql

import com.gupao.bd.sample.flink.realtime.sink.RetractSink
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala._


object RetractTest {

  def main(args: Array[String]): Unit = {
    val data = Seq("Flink", "Bob", "Bob", "something", "Hello", "Flink", "Bob")

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)

    val sink = new RetractSink

    val source = env.fromCollection(data)

    tEnv.registerDataStream("retract_test",source,'word);

    val result = tEnv.sqlQuery(
      """
        | select
        | word,
        | count(*)
        | from retract_test
        | group by word
      """.stripMargin)


    result.toRetractStream[(String,Long)].addSink(sink)


    env.execute(this.getClass.getSimpleName)






  }

}
