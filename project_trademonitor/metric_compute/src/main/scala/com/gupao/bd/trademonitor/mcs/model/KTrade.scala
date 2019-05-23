package com.gupao.bd.trademonitor.mcs.model

import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * 对应trade表中的数据
  **/
case class KTrade(consumerRecord: ConsumerRecord[String, String]) extends KRecord(consumerRecord) {

  val id = recordOfJson.getString("id").toInt

  //订单号
  val orderId = recordOfJson.getString("order_id").toInt

  //购买数量
  val buyCount = recordOfJson.getString("buy_count").toInt

  //产品ID
  val pId = recordOfJson.getString("p_id")

  //购买金额
  val totalPrice: Double = recordOfJson.getString("total_price").toDouble

  //购买时间
  val orderTime: String = recordOfJson.getString("create_time")

  //用户ID
  val userId = recordOfJson.getString("user_id").toInt

  //订单状态
  val orderStatus = recordOfJson.getString("order_status")
}
