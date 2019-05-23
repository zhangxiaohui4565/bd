package com.gupao.bigdata.spark.demo

import org.apache.commons.cli.{GnuParser, Options}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * ip url cookie time_stamp
  * input_path请替换成相应的路径
  * output_path请替换成相应的路径
  * spark-submit --master yarn-client --class com.gupao.bigdata.spark.demo.ComputeUV ./spark-demo-1.0-SNAPSHOT-uber.jar --input_path /user/root/bill/flume/events --save_mode HDFS --output_path /user/root/bill/nginx/output_uv
  * spark-submit --master yarn-client --class com.gupao.bigdata.spark.demo.ComputeUV ./spark-demo-1.0-SNAPSHOT-uber.jar --input_path /user/root/bill/flume/events --save_mode DB
  * */
object ComputeUV {

  def computeUV(textRDD: RDD[String]): RDD[(String, Long)] = {
    val splitTextFileRDD = textRDD.map(_.split("\t"))
    val result = splitTextFileRDD
      .filter(log => log(1).startsWith("http") && log(1).endsWith(".html"))
      .map(log => ((new java.net.URL(log(1))).getPath, log(2)))
      .distinct()
      .map(urlWithCookie => (urlWithCookie._1, 1L))
      .reduceByKey(_ + _)
    result
  }

  /**
    * Save to HDFS
    * */
  def saveToHDFS(result: RDD[(String, Long)], hdfsPath: String): Unit = {
    result.saveAsTextFile(hdfsPath)
  }

  /**
    * Save to Mysql
    * */
  def saveToMysql(sc: SparkContext, result: RDD[(String, Long)]): Unit = {
    val sqlContext = new SQLContext(sc)

    val props = new java.util.Properties()
    props.setProperty("driver", "com.mysql.jdbc.Driver")
    props.setProperty("user", "root")
    props.setProperty("password", "")

    import sqlContext.implicits._
    result
      .toDF("url", "count")
      .write.mode(SaveMode.Append).jdbc("jdbc:mysql://gp-bd-master01/web_data", "web_uv", props)
  }

  def main(args: Array[String]): Unit = {
    val opts = new Options
    opts.addOption("input_path", true, "Input HDFS path")
    opts.addOption("save_mode", true, "[DB|HDFS]")
    opts.addOption("output_path", true, "Output HDFS path")
    val parser = new GnuParser().parse(opts, args)

    if (parser.hasOption("input_path") == false) {
      throw new IllegalArgumentException("Missing --input_path")
    }
    if (parser.hasOption("save_mode") == false) {
      throw new IllegalArgumentException("Missing --save_mode")
    }

    if (parser.getOptionValue("save_mode") == "HDFS") {
      if (parser.hasOption("output_path") == false) {
        throw new IllegalArgumentException("Missing --output_path")
      }
    }

    val conf = new SparkConf().setAppName("ComputeUV")
    val sc = new SparkContext(conf)

    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive","true")

    val textFileRDD = sc.textFile(parser.getOptionValue("input_path"))
    val result = computeUV(textFileRDD)

    if (parser.getOptionValue("save_mode") == "HDFS") {
      saveToHDFS(result, parser.getOptionValue("output_path"))
    } else {
      saveToMysql(sc, result)
    }
  }

}