package com.gupao.bd.spark_streaming.demo.dstream

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * $ nc -lk 9999
  * 功能：从SocketInputDStream消费数据（指定IP和端口)
  **/
object NetworkWordCount {

  def main(args: Array[String]) {

    val host: String = "localhost"
    val port: Int = 9999

    //1、配置SparkConf
    val sparkConf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[2]")

    //2、构造StreamingContext
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    ssc.sparkContext.setLogLevel("WARN")
    ssc.checkpoint("checkpoint")

    //3、输入
    val lines = ssc.socketTextStream(host, port, StorageLevel.MEMORY_AND_DISK_SER)

    //4、计算
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKeyAndWindow(_ + _,Seconds(20))

    //5、输出
    wordCounts.print(10)

    wordCounts.foreachRDD((rdd,time)=>{
      val firstNum = rdd.take(11)
      // scalastyle:off println
      println("-------------------------------------------")
      println(s"Time: $time")
      println("-------------------------------------------")
      firstNum.take(10).foreach(println)
      if (firstNum.length > 10) println("...")
      println()
      // scalastyle:on println
    })

    //6、启动
    ssc.start()

    //7、终止
    ssc.awaitTermination()

  }
}
