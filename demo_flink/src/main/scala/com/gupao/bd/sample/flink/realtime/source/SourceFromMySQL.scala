package com.gupao.bd.sample.flink.realtime.source

import java.sql.{Connection, PreparedStatement}

import com.gupao.bd.sample.flink.realtime.bean.Order
import com.gupao.bd.sample.flink.realtime.utils.MysqlUtils
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

class SourceFromMySQL extends RichParallelSourceFunction[Order] {

  private  var ps:PreparedStatement = null
  private  var connection:Connection =null


  /**
    * open() 方法中建立连接，这样不用每次 invoke 的时候都要建立连接和释放连接。
    * @param parameters
    */
  override def open(parameters: Configuration): Unit = {
    connection = MysqlUtils.getConnection()
    val sql = "select * from Order;"
    ps = this.connection.prepareStatement(sql)
  }

  override def run(ctx: SourceFunction.SourceContext[Order]): Unit = {
    val resultSet = ps.executeQuery
    while (resultSet.next()) {

      val userId= resultSet.getInt("user_id")
      val productName  = resultSet.getString("product_name")
      val amount  = resultSet.getInt("amount")
      val createdTime = resultSet.getInt("created_time")

      ctx.collect(new Order(userId,productName,amount,createdTime))
    }
  }

  override def close(): Unit = {
    if(connection !=null){
      connection.close()
    }
    if(ps !=null){
      ps.close()
    }
  }

  override def cancel(): Unit = {}



}
