package com.gupao.bd.spark_streaming.demo.dstream

import org.apache.log4j.{Level, Logger}

trait StreamingExample extends App {

  Logger.getRootLogger.setLevel(Level.WARN)

  val hostname = "localhost"
  val port = 9999
  val checkpointDir = "checkpoint"
  val outputDir = "output"
  val inputDir = "input"

}
