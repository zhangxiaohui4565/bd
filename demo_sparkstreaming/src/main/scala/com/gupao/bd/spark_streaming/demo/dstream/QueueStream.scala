package com.gupao.bd.spark_streaming.demo.dstream

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

/**
  * 功能：从QueueInputDStream消费数据
  */
object QueueStream extends StreamingExample {

  //1、设置参数
  val sparkConf = new SparkConf().setMaster("local[3]").setAppName("QueueStream")

  //2、创建StreamingContext
  val ssc = new StreamingContext(sparkConf, Seconds(2))
  val rddQueue = new mutable.Queue[RDD[Int]]()

  //3、指定输入
  val inputStream = ssc.queueStream(rddQueue)

  //4、构造工作流(DAG)
//  inputStream.glom().map(x=>(x(0),x(1))).print()

//  val mappedStream = inputStream.map(x => (x % 10, 1))
//  val reducedStream = mappedStream.reduceByKey(_ + _)
//  reducedStream.print()

  //5、启动应用程序
  ssc.start()

  //构造rddQueue
  for (i <- 1 to 30) {
    rddQueue.synchronized {
      rddQueue += ssc.sparkContext.makeRDD(1 to 1000, 10)
    }
    Thread.sleep(1000)
  }

  //6、关闭应用程序
  ssc.stop()
}
