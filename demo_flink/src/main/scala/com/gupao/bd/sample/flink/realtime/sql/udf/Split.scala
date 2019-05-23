package com.gupao.bd.sample.flink.realtime.sql.udf

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.functions.TableFunction
import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row


/**
  * 自定义Split 表函数
  * @param separator
  */
class Split(separator: String) extends TableFunction[(String, Int)] {
  def eval(str: String): Unit = {
    str.split(separator).foreach(x => collect(x, x.length))
  }
}

case class SplitData(id:Long,numbers:String)

object SplitTest{
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)
    val data1 = Seq(
      SplitData(1001,"11030,1031,1032,1033,1190"),
      SplitData(1002,"2030,2031,2032,2033,2190"))

    val splitDs:DataStream[SplitData]  = env.fromCollection[SplitData](data1)

    tEnv.registerDataStream("splitTable",splitDs,'id,'numbers)

    tEnv.registerFunction("split", new Split(","))

    val sql =
      """
        |SELECT
        |  id,
        |  word,
        |  length
        |  FROM splitTable, LATERAL TABLE(split(numbers)) as T(word,length)
      """.stripMargin

    val result = tEnv.sqlQuery(sql)

    result.toAppendStream[Row].print()

    env.execute()

  }
}