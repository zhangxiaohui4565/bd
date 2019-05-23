package com.gupao.bigdata.spark.demo

import java.util.concurrent.atomic.AtomicInteger

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

import scala.util.Random

object DataSkewDemo {

  val dataSize = 5000000
  val userCount = 1000
  val users = for (i <- 1 to userCount) yield i.toLong
  val cities = Array("北京市","天津市","上海市","重庆市","河北省","山西省","辽宁省","吉林省","黑龙江省","江苏省","浙江省","安徽省","福建省","江西省","山东省","河南省","湖北省","湖南省","广东省","海南省","四川省","贵州省","云南省","陕西省","甘肃省","青海省","台湾省","内蒙古","广西","西藏","宁夏","新疆","香港","澳门","台湾")

  /**
    * 构造有数据倾斜的数据集
    * 95%的数据为0用户的访问数据，剩下的为随机数据
    * (用户id, 1)
    * */
  def generateSkewUserAccessData(): Seq[(Long, Int)] = {
    for (i <- 0 until dataSize) yield {
      if (i < dataSize * 0.95)
        (users(0), 1)
      else
        (users(i % userCount), 1)
    }
  }

  /**
    * 生成用户信息数据
    * (用户id, 城市名)
    * */
  def generateUserInfoData(): Seq[(Long, String)] = {
    val rand = new Random()
    for (i <- 0 until userCount) yield {
        (users(i), cities(i % cities.size))
    }
  }

  /**
    * 统计每个用户访问数
    * */
  def sumAccessCountByUser(accessData: RDD[(Long, Int)]) = {
    accessData
      .reduceByKey(_ + _)
      .map(tuple => (tuple._2, tuple._1))
      .sortByKey(false)
      .take(10)
      .foreach(println)
  }

  /**
    * 直接使用join计算每个城市访问数
    * */
  def normalJoin(accessData: RDD[(Long, Int)], userInfoData: RDD[(Long, String)]) = {
    val aggRDD = accessData
      .join(userInfoData)
      .map(joinInfo => (joinInfo._2._2, joinInfo._2._1))
      .reduceByKey(_ + _)
      .collect()
      .foreach(println)
  }

  /**
    * 对倾斜数据集accessData随机增加N种前缀，对userInfoData扩增N倍
    * */
  def randomAddNPrefixForAllJoin(accessData: RDD[(Long, Int)], userInfoData: RDD[(Long, String)]) = {
    val addNPrefixRdd = accessData.mapPartitions(iter => {
      var inc = 0
      iter.map(x => {
        inc += 1
        (inc % 8 + ","  + x._1, x._2)
      })
    })

    val increaseNSizeUserInfoRdd = userInfoData.flatMap(item => for (i <- 0 until 8) yield (i + "," + item, item._2))

    addNPrefixRdd
      .join(increaseNSizeUserInfoRdd)
      .map(joinInfo => (joinInfo._2._2, joinInfo._2._1))
      .reduceByKey(_ + _)
      .collect()
      .foreach(println)
  }

  /**
    * 对倾斜数据集accessData随机增加N种前缀，对userInfoData扩增N倍
    * */
  def randomAddNPrefixForSkewJoin(accessData: RDD[(Long, Int)], userInfoData: RDD[(Long, String)]) = {
    val addNPrefixRdd = accessData.mapPartitions(iter => {
      var inc = 0
      iter.map(x => {
        if (x._1 == 1) {
          inc += 1
          (inc % 8 + ","  + x._1, x._2)
        } else {
          ((1, x._1), x._2)
        }
      })
    })

    val increaseNSizeUserInfoRdd = userInfoData.flatMap(item => {
      if (item._1 == 0) {
        for (i <- 0 until 8) yield (i + "," + item, item._2)
      } else {
        Seq(((1, item._1), item._2))
      }
    })

    addNPrefixRdd
      .join(increaseNSizeUserInfoRdd)
      .map(joinInfo => (joinInfo._2._2, joinInfo._2._1))
      .reduceByKey(_ + _)
      .collect()
      .foreach(println)
  }

  /**
    * 使用mapSideJoin计算每个城市订单
    * */
  def solution1MapSideJoin(sc: SparkContext, accessData: RDD[(Long, Int)], userInfoData: RDD[(Long, String)]) = {
    val brd = sc.broadcast(userInfoData.collectAsMap())
    accessData.mapPartitions(iter => {
      val userInfoMap = brd.value
      iter.map(accessInfo => (userInfoMap.get(accessInfo._1).get, accessInfo._2))
    }).reduceByKey(_ + _)
      .collect()
      .foreach(println)
  }

  /**
    * 抽样查看key分布情况
    * */
  def sampleDataFindSkewKey(accessData: RDD[(Long, Int)]): Unit = {
    accessData
      .sample(false, 0.01)
      .mapValues(_ => 1L).reduceByKey(_ + _)
      .map(x => (x._2, x._1))
      .sortByKey(false)
      .top(10)
      .foreach(println)
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("DataSkewDemo").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val userAccessRDD = sc.parallelize(generateSkewUserAccessData(), 8)
    val userInfoDataRDD = sc.parallelize(generateUserInfoData(), 8)

//    sampleDataFindSkewKey(userAccessRDD)
    normalJoin(userAccessRDD, userInfoDataRDD)
    randomAddNPrefixForAllJoin(userAccessRDD, userInfoDataRDD)
    randomAddNPrefixForSkewJoin(userAccessRDD, userInfoDataRDD)
    solution1MapSideJoin(sc, userAccessRDD, userInfoDataRDD)
//    sumAccessCountByUser(userAccessRDD)

    Console.readInt
  }

}