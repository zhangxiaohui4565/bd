package com.gupao.bd.spark_streaming.demo.dstream

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
import java.nio.charset.StandardCharsets

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.receiver.Receiver

/**
  * 功能：自定义Receiver
  **/
object CustomReceiver extends StreamingExample {

  val sparkConf = new SparkConf().setAppName("CustomReceiver")
  val ssc = new StreamingContext(sparkConf, Seconds(1))

  val lines = ssc.receiverStream(new CustomReceiver(hostname, port))

  val words = lines.flatMap(_.split(" "))
  val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
  wordCounts.print()
  ssc.start()
  ssc.awaitTermination()

}

class CustomReceiver(host: String, port: Int)
  extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) {

  def onStart() {
    new Thread("Socket Receiver") {
      override def run() {
        receive()
      }
    }.start()
  }

  def onStop() {
    //todo
  }

  /** Create a socket connection and receive data until receiver is stopped */
  private def receive() {
    var socket: Socket = null
    var userInput: String = null
    try {
      println(s"Connecting to $host : $port")
      socket = new Socket(host, port)
      println(s"Connected to $host : $port")
      val reader = new BufferedReader(
        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
      userInput = reader.readLine()
      while (!isStopped && userInput != null) {
        store(userInput)
        userInput = reader.readLine()
      }
      reader.close()
      socket.close()
      println("Stopped receiving")
      restart("Trying to connect again")
    } catch {
      case e: java.net.ConnectException =>
        restart(s"Error connecting to $host : $port", e)
      case t: Throwable =>
        restart("Error receiving data", t)
    }
  }
}
