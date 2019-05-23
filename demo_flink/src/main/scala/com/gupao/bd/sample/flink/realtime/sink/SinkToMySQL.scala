package com.gupao.bd.sample.flink.realtime.sink

import java.sql.{Connection, PreparedStatement}

import com.gupao.bd.sample.flink.realtime.bean.Order
import com.gupao.bd.sample.flink.realtime.utils.MysqlUtils
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

class SinkToMySQL extends  RichSinkFunction[Order]{
  private  var ps:PreparedStatement = null
  private  var  connection:Connection =null

  //方法中建立连接，这样不用每次 invoke 的时候都要建立连接和释放连接。
  override def open(parameters: Configuration): Unit = {
    connection = MysqlUtils.getConnection()
    val sql = "insert into Order(user_id, product_name, amount, created_time) values(?, ?, ?, ?);"
    ps = this.connection.prepareStatement(sql)
  }

  //循环调用
  override def invoke(value: Order, context: SinkFunction.Context[_]): Unit = {
    ps.setInt(1, value.user)
    ps.setString(2, value.product)
    ps.setInt(3, value.amount)
    ps.setLong(4, value.rowtime)
  }

  //关闭连接和释放资源
  override def close(): Unit = {
    if (connection != null) connection.close
    if (ps != null) ps.close
  }


}
