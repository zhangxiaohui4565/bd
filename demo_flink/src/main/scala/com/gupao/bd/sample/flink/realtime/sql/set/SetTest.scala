package com.gupao.bd.sample.flink.realtime.sql.set

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.table.api.TableEnvironment

object SetTest {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val tableEnv = TableEnvironment.getTableEnvironment(env)
    env.setParallelism(1)
//    val left = tableEnv.fromDataSet(ds1, "a, b, c")
//    val right = tableEnv.fromDataSet(ds2, "a, b, c")
//    val result = left.union(right)

  }

}
