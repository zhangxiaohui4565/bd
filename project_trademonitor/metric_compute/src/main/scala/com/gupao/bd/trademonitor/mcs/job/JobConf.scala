package com.gupao.bd.trademonitor.mcs.job

import java.io.{File, FileInputStream}
import java.util.Properties

/**
  * 功能 ： kafka/spark/hbase相关的配置信息
  **/
class JobConf(fileName: String) extends Serializable {

  @transient val file = new File(fileName)
  @transient val fis = new FileInputStream(file)
  val properties = new Properties()
  properties.load(fis)
  fis.close()

  val KAFKA_BOOTSTRAP_SERVERS = properties.getProperty("kafka.bootstrap.servers")
  val KAFKA_TOPIC_METRIC = properties.getProperty("kafka.topic.metric")
  val KAFKA_TOPIC_USER = properties.getProperty("kafka.topic.user")
  val KAFKA_TOPIC_TRADE = properties.getProperty("kafka.topic.trade")
  val KAFKA_TOPIC_PRODUCT = properties.getProperty("kafka.topic.product")
  val HBASE_ZOOKEEPER_QUORUM = properties.getProperty("hbase.zookeeper.quorum")
  val SPARK_MASTER = properties.getProperty("spark.master")
  val SPARK_CHECKPOINT_DIR = properties.getProperty("spark.checkpoint.dir")
  val ENGINE_NAME = properties.getProperty("engine.name")
}
