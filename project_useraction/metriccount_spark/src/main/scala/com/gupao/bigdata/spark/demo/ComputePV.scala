package com.gupao.bigdata.spark.demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.eclipse.jetty.util.{MultiMap, UrlEncoded}

/**
  * ip,url,cookie,time_stamp
  * spark-submit --master yarn-client --class com.gupao.bigdata.spark.demo.ComputePV ./spark-demo-1.0-SNAPSHOT.jar /user/root/bill/nginx/input /user/root/bill/nginx/output
  * */
object ComputePV {

  def computePV(nginxRDD: RDD[String], opencartRDD: RDD[String]): RDD[(String, String, Long)] = {
    val splitNginxRDD = nginxRDD.map(_.split("\t"))
    val splitOpencartRDD = opencartRDD.map(_.split(",")).map(x => (x(0), x(1)))
    val countResultRDD = splitNginxRDD
      .filter(log => log(1).contains("product_id"))
      .map(log => {
        val paramsMap = new MultiMap[String]
        UrlEncoded.decodeTo(log(1), paramsMap, "UTF-8")
        val productId = paramsMap.getValue("product_id", 0);
        (productId, 1L)
      })
      .reduceByKey(_ + _)
    val result = splitOpencartRDD.join(countResultRDD).map(x => (x._1, x._2._1, x._2._2))
    result
  }

  def main(args: Array[String]): Unit = {
    if(args.length < 3){
      System.err.println("Usage:<nginx_input_file> <opencart_product_desc_file> <output_file>")
      System.exit(1)
    }
    val conf = new SparkConf().setAppName("ComputePV")
    val sc = new SparkContext(conf)
    val nginxFileRDD = sc.textFile(args(0))
    val opencartFileRDD = sc.textFile(args(1))
    val result = computePV(nginxFileRDD, opencartFileRDD)
    result.map(r => r._1 + "\t" + r._2 + "\t" + r._3).saveAsTextFile(args(2))
  }

}