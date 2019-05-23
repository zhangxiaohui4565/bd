package com.gupao.bd.spark_streaming.demo.dstream

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

object OnlineBlackListFilter {

  def main(args: Array[String]) {

    if (args.length < 2) {
      println("set host and port")
      System.exit(1)
    }

    val host: String = args(0)
    val port: Int = args(1).toInt

    //1、配置SparkConf
    val sparkConf = new SparkConf().setAppName("OnlineBlackListFilter")

    //2、构造StreamingContext
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val sc = ssc.sparkContext
    sc.setLogLevel("WARN")

    val blackList = sc.broadcast(Array("shit", "fuck", "dead"))

    val accum = sc.longAccumulator("BlackListAccum")

    //3、输入
    val comment = ssc.socketTextStream(host, port, StorageLevel.MEMORY_AND_DISK_SER)

    //4、计算
    val words = comment.foreachRDD((rdd, time) => {
      rdd.foreachPartition(par => {
        par.foreach(record => {
          val words = record.split(" ")
          val result = blackList.value.intersect(words)
          if (result.length > 0) {
            accum.add(1L)
          }
        })
      })
    })

    //6、启动
    ssc.start()

    //7、终止
    ssc.awaitTermination()
  }
}
