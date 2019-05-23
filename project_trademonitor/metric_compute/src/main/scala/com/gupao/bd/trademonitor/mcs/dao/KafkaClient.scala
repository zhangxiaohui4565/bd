package com.gupao.bd.trademonitor.mcs.dao

import java.util._

import com.gupao.bd.trademonitor.mcs.metric.MRecord
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

/**
  * 功能：封装kafka consumer/producer客户端,负责和kafka集群进行交互
  */
class KafkaClient(bootstrapServers: String) {
  private var producer: KafkaProducer[String, String] = _
  private var consumer: KafkaConsumer[String, String] = _

  private def initProducer: KafkaProducer[String, String] = {
    val prop: Properties = new Properties
    prop.put("bootstrap.servers", this.bootstrapServers)
    prop.put("key.serializer", classOf[StringSerializer].getName)
    prop.put("value.serializer", classOf[StringSerializer].getName)
    prop.put("acks", "1")
    prop.put("retries", "1")
    new KafkaProducer[String, String](prop)
  }

  private def initConsumer(consumerGrp: String): KafkaConsumer[String, String] = {
    val prop: Properties = new Properties
    prop.put("bootstrap.servers", this.bootstrapServers)
    prop.put("group.id", consumerGrp)
    prop.put("key.deserializer", classOf[StringDeserializer].getName)
    prop.put("value.deserializer", classOf[StringDeserializer].getName)
    prop.put("enable.auto.commit", "true")
    prop.put("auto.commit.interval.ms", "1000")
    prop.put("auto.offset.reset", "latest")
    new KafkaConsumer[String, String](prop)
  }

  def produce(topic: String, value: MRecord) {
    produce(topic, null, value.toString)
  }

  def produce(topic: String, value: String) {
    produce(topic, null, value)
  }

  def produce(topic: String, key: String, value: String) {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, key, value)
    if (this.producer == null) this.producer = initProducer
    this.producer.send(record).get()
  }

  def consume(topics: List[String], consumerGroup: String): ConsumerRecords[String, String] = {
    if (this.consumer == null) this.consumer = initConsumer(consumerGroup)
    consumer.subscribe(topics)
    consumer.poll(500)
  }

}
