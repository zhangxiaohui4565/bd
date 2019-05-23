package com.gupao.bd.trademonitor.mcs.model

import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * 对应user表中的数据
  **/
case class KUser(consumerRecord: ConsumerRecord[String, String]) extends KRecord(consumerRecord) {

  val id: String = recordOfJson.getString("id")

  //用户名
  val userName = recordOfJson.getString("user_name")

  //用户所在城市
  val city = recordOfJson.getString("city")
}
