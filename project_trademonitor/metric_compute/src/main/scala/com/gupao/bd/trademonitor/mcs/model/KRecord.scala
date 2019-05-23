package com.gupao.bd.trademonitor.mcs.model

import com.alibaba.fastjson.{JSONObject, JSON}
import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * 代表从kafka获取的数据
  */
abstract class KRecord(consumerRecord: ConsumerRecord[String, String]) {

  //从ConsumerRecord中获取value，value对应的是简化后的binlog信息
  private val binlogOfJson = JSON.parse(consumerRecord.value()).asInstanceOf[JSONObject]

  //从binlog中获取insert后的数据，即当前表中的记录
  val recordOfJson: JSONObject = binlogOfJson.getJSONObject("after")

  override def toString: String = recordOfJson.toJSONString
}
