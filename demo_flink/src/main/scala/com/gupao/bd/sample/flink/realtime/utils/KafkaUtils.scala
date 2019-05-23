package com.gupao.bd.sample.flink.realtime.utils

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

object KafkaUtils {

  import java.lang.management.ManagementFactory

  val mxBean = ManagementFactory.getOperatingSystemMXBean

  def main(args: Array[String]): Unit = {

  }

  def produceData(topicName:String,message:String): Unit ={
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("retries", "0")
    props.put("batch.size", "16384")
    props.put("linger.ms", "1")
    props.put("buffer.memory", "33554432")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String,String](props)

    producer.send(new ProducerRecord[String,String](topicName,message),new Callback {
      override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
        if(e !=null){
          println("Failed to send message with exception " + e)
        }
      }
    })

    producer.close()
  }






}