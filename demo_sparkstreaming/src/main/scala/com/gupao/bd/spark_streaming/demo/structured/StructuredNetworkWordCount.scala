package com.gupao.bd.spark_streaming.demo.structured

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{Trigger, OutputMode}

//nc -lk 9999
object StructuredNetworkWordCount {

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder
      .master("local[2]")
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    val lines = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .load()

    // Split the lines into words
    val words = lines.as[String].flatMap(_.split(" "))

    val isSQL = true

    if (isSQL) {
      words.createOrReplaceTempView("t")
      val wordCounts = lines.sqlContext.sql("select value,count(*) as  cnt from t group by value")

      // Start running the query that prints the running counts to the console
      val query = wordCounts.writeStream
        .outputMode(OutputMode.Update())
        .format("console")
        .start()

      query.awaitTermination()
    } else {
      // Generate running word count
      val wordCounts = words.groupBy("value").count()

      // Start running the query that prints the running counts to the console
      val query = wordCounts.writeStream
        .outputMode(OutputMode.Update())
        .format("console")
        .start()

      query.awaitTermination()
    }

  }
}
