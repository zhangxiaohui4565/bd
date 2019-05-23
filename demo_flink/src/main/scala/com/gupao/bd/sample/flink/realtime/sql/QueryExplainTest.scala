package com.gupao.bd.sample.flink.realtime.sql

import com.gupao.bd.sample.flink.realtime.bean.Order
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala._

object QueryExplainTest {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)
    val orderASeq = Seq(
      Order(1,"beer",3,10L),
      Order(1,"diaper",4,11L),
      Order(3,"rubber",2,12L))

    val orderBSeq = Seq(
      Order(2,"beer",3,10L),
      Order(2,"diaper",3,11L),
      Order(4,"rubber",1,12L))
    val orderA:DataStream[Order]  = env.fromCollection[Order](orderASeq)

    val orderB:DataStream[Order]  = env.fromCollection[Order](orderBSeq)

    tEnv.registerDataStream("orderA",orderA,'user,'product,'amount,'rowtime)
    tEnv.registerDataStream("orderB",orderB,'user,'product,'amount,'rowtime)

    val sql =
      """
        |select *
        |from (
        |   select *
        |   from orderA
        |   where user < 3
        |   union all
        |   select *
        |   from orderB
        |   where product <> 'rubber' )
        |   as orderAll
        |   where amount >2
      """.stripMargin

    val result = tEnv.sqlQuery(sql)

     println(tEnv.explain(result))
     //result.toAppendStream[Order].print()

     env.execute(getClass.getSimpleName)

  }

}
