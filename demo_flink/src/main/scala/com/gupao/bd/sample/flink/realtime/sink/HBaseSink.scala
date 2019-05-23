package com.gupao.bd.sample.flink.realtime.sink


import com.gupao.bd.sample.flink.realtime.bean.OrderAmount
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}

class HBaseSink extends RichSinkFunction[OrderAmount] {

  private val family = "info"
  private var connection: Connection = null

  override def open(parameters: Configuration): Unit = {
    if (connection == null) {
      val hbaseConfig = HBaseConfiguration.create()
      hbaseConfig.set("hbase.zookeeper.quorum", "localhost") // set zookeeper nodes
      hbaseConfig.set("hbase.zookeeper.property.clientPort", "2181") // set zookeeper port
      connection = ConnectionFactory.createConnection(hbaseConfig)
    }
    connection
  }


  //循环调用
  override def invoke(orders: OrderAmount, context: SinkFunction.Context[_]): Unit = {
    val table = connection.getTable(TableName.valueOf("demo_flink:order_amount"))
    val rowkey = orders.dt.getTime  + "_"+ orders.categoryId
    val put = new Put(Bytes.toBytes(rowkey))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes("orderNum"), Bytes.toBytes(orders.orderNum+""))
    put.addColumn(Bytes.toBytes(family), Bytes.toBytes("orderAmount"), Bytes.toBytes(orders.orderAmoumt+""))
    table.put(put)
    table.close()

  }

  //关闭连接和释放资源
  override def close(): Unit = {
  }


}
