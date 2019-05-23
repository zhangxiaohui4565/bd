package com.gupao.bd.spark_streaming.demo.dstream

import java.io.File
import java.nio.charset.Charset

import com.google.common.io.Files
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 功能：演示如何从checkpoint中重建应用程序及broadcast和accumulator的用法
  * nc -lk 9999
  **/
object RecoverableNetworkWordCount extends StreamingExample {

  // 如果设置了checkpoint，则从checkpoint中恢复应用程序；否则，重新创建应用程序
  val ssc = StreamingContext.getOrCreate(checkpointDir,
    () => createContext(hostname, port, outputDir, checkpointDir))
//  ssc.sparkContext.setLogLevel("WARN")
  ssc.start()
  ssc.awaitTermination()

  def createContext(ip: String, port: Int, outputPath: String, checkpointDirectory: String): StreamingContext = {
    println("Creating new context .....")

    //创建输出文件夹
    val outputFile = new File(outputPath)
    if (outputFile.exists()) outputFile.delete()

    val sparkConf = new SparkConf().setMaster("local[3]").setAppName("RecoverableNetworkWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint(checkpointDirectory)

    val lines = ssc.socketTextStream(ip, port,StorageLevel.MEMORY_AND_DISK)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map((_, 1)).reduceByKey(_ + _)


    wordCounts.foreachRDD { (rdd: RDD[(String, Int)], time: Time) =>

      // 获取黑名单
      val blacklist = WordBlacklist.getInstance(rdd.sparkContext)

      // 黑名单计数器
      val droppedWordsCounter = DroppedWordsCounter.getInstance(rdd.sparkContext)

      // 排除黑名单单词
      val counts = rdd.filter { case (word, count) =>
        if (blacklist.value.contains(word)) {
          droppedWordsCounter.add(count)
          false
        } else {
          true
        }
      }.collect().mkString("[", ", ", "]")

      // 输出过滤后的word
      val output = s"Counts at time $time $counts"
      println(output)
      println(s"Dropped ${droppedWordsCounter.value} word(s) totally")
      println(s"Appending to ${outputFile.getAbsolutePath}")
      Files.append(output + "\n", outputFile, Charset.defaultCharset())
    }

    ssc
  }
}


object WordBlacklist {

  @volatile private var instance: Broadcast[Seq[String]] = null

  def getInstance(sc: SparkContext): Broadcast[Seq[String]] = {
    if (instance == null) {
      synchronized {
        if (instance == null) {
          val wordBlacklist = Seq("a", "b", "c")
          instance = sc.broadcast(wordBlacklist)
        }
      }
    }
    instance
  }
}

object DroppedWordsCounter {

  @volatile private var instance: LongAccumulator = _

  def getInstance(sc: SparkContext): LongAccumulator = {
    if (instance == null) {
      synchronized {
        if (instance == null) {
          instance = sc.longAccumulator("WordsInBlacklistCounter")
        }
      }
    }
    instance
  }
}


