package com.gupao.bigdata.spark.demo

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfter, FunSuite}

class ComputePVTest extends FunSuite with BeforeAndAfter {

  private val master = "local[2]"
  private val appName = "ComputePV-Test"

  private var sc: SparkContext = _

  before {
    val conf = new SparkConf()
      .setMaster(master)
      .setAppName(appName)

    sc = new SparkContext(conf)
  }

  after {
    if (sc != null) {
      sc.stop()
    }
  }

  test("pv compute") {
    // ip,url,cookie,time_stamp
    val seq = Seq(
      "183.193.62.196\thttp://gp-bd-master01.com/page1.html\tuid=7A1313ACD1D3F15A9103FD7A02040303\t25/May/2018:21:04:11 +0800",
      "183.193.62.196\thttp://gp-bd-master01.com/page1.html\tuid=7A1313ACD1D3F15A9103FD7A02040303\t25/May/2018:21:04:11 +0800",
      "83.242.244.70\thttp://gp-bd-master01.com/favicon.ico\t-\t25/May/2018:21:05:25 +0800",
      "183.193.62.196\t-\tuid=7A1313ACD1D3F15A9103FD7A02040303\t25/May/2018:21:05:27 +0800",
      "183.193.62.196\thttp://gp-bd-master01.com/page2.html\tuid=7A1313ACD1D3F15A9103FD7A02040303\t25/May/2018:21:05:27 +0800"
    )
    val testLogRDD = sc.parallelize(seq)
    val resultRDD = ComputePV.computePV(testLogRDD)
    val result = resultRDD.collect()
    val keyValueMap = result.toMap
    print(keyValueMap)
    assert(keyValueMap("/page1.html") == 2)
    assert(keyValueMap("/page2.html") == 1)
  }

}
