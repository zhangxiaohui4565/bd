package com.gupao.bd.sample.flink.realtime.sql

import java.sql.Timestamp
import java.util.Properties

import com.gupao.bd.sample.flink.realtime.bean.{OrderAmount, Orders}
import com.gupao.bd.sample.flink.realtime.sink.HBaseSink
import com.gupao.bd.sample.flink.realtime.sql.udf.Add
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.api.scala._


/**
  * 有效订单统计金额及订单量统计
  *
  * hbase:
  * create_namespace 'demo_flink'
  * create 'demo_flink:order_amount', 'info'
  */

object OrderPayCount {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    val props = new Properties()
    props.setProperty("bootstrap.servers", "localhost:9092")
    props.put("auto.offset.reset", "latest")
    props.setProperty("group.id", "flink-kafka-test")

    env.setParallelism(2)

    val consumer = new FlinkKafkaConsumer011[String]("flink-test-orders", new SimpleStringSchema(), props)

    val  orders = env
      .addSource(consumer)
      .map(v => {
        val tokens = v.split(",")
        Orders(tokens(0).toInt, tokens(1), tokens(2).toInt,tokens(3).toDouble,tokens(4).toInt,tokens(5).toInt,new Timestamp(tokens(6).toLong))
      })
      .assignAscendingTimestamps(_.orderTime.getTime)
      .toTable(tEnv, 'orderId, 'productName, 'productNum, 'amount, 'isPay, 'categoryId, 'orderTime.rowtime)


    tEnv.registerFunction("add", new Add(3))
    tEnv.registerTable("Orders", orders)

    val sql =
      """
        |SELECT
        | HOP_START(orderTime, INTERVAL '30' SECOND, INTERVAL '1' MINUTE),
        | add(categoryId),
        | COUNT(orderId),
        | SUM(productNum*amount)
        | FROM Orders
        | WHERE isPay=1
        | GROUP BY HOP(orderTime, INTERVAL '30' SECOND, INTERVAL '1' MINUTE), categoryId
        |""".stripMargin


    val resultTable= tEnv.sqlQuery(sql)

    resultTable.toAppendStream[OrderAmount].addSink(new HBaseSink)

    env.execute()
  }

}


