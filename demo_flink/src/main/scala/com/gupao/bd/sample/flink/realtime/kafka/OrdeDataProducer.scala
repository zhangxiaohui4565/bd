package com.gupao.bd.sample.flink.realtime.kafka

import java.sql.Timestamp

import com.gupao.bd.sample.flink.realtime.utils.KafkaUtils

import scala.collection.mutable
import scala.util.Random

object OrdeDataProducer {


   val productNames = Array("iphone8p","mbp2018","locklock cup","dell computer","iphon7p")
   val amounts = Array(6888,16888,25,3,6500,700)
   val random = new Random()


  def main(args: Array[String]): Unit = {
    var i = 1
    while(true){
      val currentTs = System.currentTimeMillis()-36000
      val sb = new mutable.StringBuilder()
      sb.append(100 + i).append(",")
      sb.append(productNames(random.nextInt(5))).append(",")
      sb.append(1 + random.nextInt(3)).append(",")
      sb.append(amounts(random.nextInt(5))).append(",")
      sb.append(i%3).append(",")
      sb.append(1000+random.nextInt(10)).append(",")
      sb.append(currentTs).append(",")
      KafkaUtils.produceData("flink-test-orders",sb.toString())
      println(sb.toString())
      Thread.sleep(1000)
      i += 1
    }


  }

}
