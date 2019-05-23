package com.gupao.bd.spark_streaming.demo.structured

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{ProcessingTime, OutputMode}

//nc -lk 9999
object StructuredNetworkWordCount2 {

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder
      .master("local[2]")
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    val query = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()
      .as[String]
      .flatMap(_.split(" "))
      .groupBy("value").count()
      .writeStream.trigger(ProcessingTime("10 seconds")).
      outputMode(OutputMode.Complete()).
      format("console")
      .start()

    query.awaitTermination()
  }
}
