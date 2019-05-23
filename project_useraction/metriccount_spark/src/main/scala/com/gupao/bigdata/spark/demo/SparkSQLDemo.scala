package com.gupao.bigdata.spark.demo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

object SparkSQLDemo {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setAppName("DataFrameSQL").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    // 使用SQL查询平均分
    import sqlContext.implicits._
    val df = Seq(
      (1, "Bill", "Computer Science", 100),
      (2, "Bill", "Math", 85),
      (3, "Bill", "English", 90),
      (4, "Peter", "Math", 82),
      (5, "Peter", "Music", 90),
      (6, "Peter", "Chemistry", 85)
    ).toDF("id", "name", "course", "score")

    df.registerTempTable("course_score")
    sqlContext.sql("select name, avg(score) from course_score group by name").show()

    // 直接使用DataFrame API
    df.groupBy("name")
      .avg("score")
      .show()
  }

}
