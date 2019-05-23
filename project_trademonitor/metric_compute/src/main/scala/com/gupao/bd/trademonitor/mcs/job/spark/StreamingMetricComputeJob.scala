package com.gupao.bd.trademonitor.mcs.job.spark

import com.gupao.bd.trademonitor.mcs.dao.KafkaClient
import com.gupao.bd.trademonitor.mcs.job.JobConf
import com.gupao.bd.trademonitor.mcs.metric._
import com.gupao.bd.trademonitor.mcs.model._
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming._

/**
  * 功能：从kafka各topic消费数据，完成对应统计指标的实时计算(spark streaming版）
  **/
class StreamingMetricComputeJob(jobConf: JobConf) extends BaseStreamingJob(jobConf) {

  val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
  val BATCH_INTERVAL = Seconds(5)

  override def run(): Unit = {

    val ssc = createStreamingContext("StreamingMetricComputeJob", BATCH_INTERVAL)

    //老客列表,广播到executor端
    val customerList = ssc.sparkContext.broadcast(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

    //窗口操作和全局累计需要设置checkpoint目录
    ssc.checkpoint(jobConf.SPARK_CHECKPOINT_DIR)

    analyze(ssc, customerList)

    ssc.start()
    ssc.awaitTermination()

  }

  /**
    * 计算指标
    **/
  def analyze(ssc: StreamingContext, customerList: Broadcast[List[Int]]) = {

    // 通用函数：将计算好的指标输出到kafka
    val outputFun = (records: Iterator[(String, AnyVal)], time: Time, mtype: MType) => {

      val dt = DateFormatUtils.format(time.milliseconds, DATE_FORMAT)
      val kafkaClient = new KafkaClient(jobConf.KAFKA_BOOTSTRAP_SERVERS)

      records.foreach(record => {
        val (key, value) = record

        // 打印日志，便于监控
        println(s"$dt -> ${mtype.desc}#$key : $value")

        // 指标推送kafka
        kafkaClient.produce(jobConf.KAFKA_TOPIC_METRIC, MRecord(mtype, key, value, dt))

      })

    }

    // 计算各城市累计注册用户数
    getKafkaDStream(ssc, jobConf.KAFKA_TOPIC_USER)
      .map(record => KUser(record))
      .map(user => (user.city, 1))
      .updateStateByKey((currValues: Seq[Int], prevValueState: Option[Int]) => {

        //当前批次注册人数（A)
        val currentCount = currValues.sum

        //历史累计注册人数 (B)
        val previousCount = prevValueState.getOrElse(0)

        //截止目前累计注册人数(C) = A + B
        Some(currentCount + previousCount)

      }).foreachRDD((rdd, time) => {
      // 注意foreachRDD的正确使用姿势
      // 参考官方文档：http://spark.apache.org/docs/latest/streaming-programming-guide.html#design-patterns-for-using-foreachrdd
      rdd.foreachPartition(outputFun(_, time, MAccRegisterUserCntPerCity))
    })

    // 获取交易数据
    val tradeDStream = getKafkaDStream(ssc, jobConf.KAFKA_TOPIC_TRADE)
      .map(record => KTrade(record))

    // 累加函数
    val mappingFun = (key: String, value: Option[Double], state: State[Double]) => {
      val sum = value.getOrElse(0d) + state.getOption().getOrElse(0d)
      val output = (key, sum)
      state.update(sum)
      output
    }

    // 全站累计成交额
    tradeDStream.map(trade => ("全站", trade.totalPrice))
      .mapWithState(StateSpec.function(mappingFun)) //注意与updateStateByKey的区别
      .foreachRDD((rdd, time) => {
      rdd.foreachPartition(outputFun(_, time, MAccTradePrice))
    })

    // 全站分时段增量成交额
    tradeDStream.map(trade => ("过去10s", trade.totalPrice))
      .reduceByKeyAndWindow((a, b) => a + b, BATCH_INTERVAL * 2) //batch间隔是5秒，窗口宽度是10秒
      .foreachRDD((rdd, time) => {
      rdd.foreachPartition(outputFun(_, time, MIncTradePrice))
    })

    // 各产品累计成交额
    //    val conf = HBaseConfiguration.create()
    //    conf.set("hbase.zookeeper.quorum", jobConf.HBASE_ZOOKEEPER_QUORUM)

    //设置查询的表名
    //    conf.set(TableInputFormat.INPUT_TABLE, "wuchen:gp_product")

    //    val prdRDD = ssc.sparkContext.newAPIHadoopRDD(conf, classOf[TableInputFormat],
    //      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
    //      classOf[org.apache.hadoop.hbase.client.Result])
    //      .map(record => (record._1.toString, Bytes.toString(record._2.getValue("info".getBytes, "p_name".getBytes))))

    tradeDStream.map(trade => (trade.pId, trade.totalPrice))
      .mapWithState(StateSpec.function(mappingFun))
      .map(result => (result._1 match {
        case "1" => "iPhone"
        case "2" => "小米"
        case "3" => "OPPO"
        case "4" => "华为"
        case _ => "未知"
      }, result._2))
      .foreachRDD((rdd, time) => {
        //        val joinedRDD = rdd.join(prdRDD).map(r => (r._2._2, r._2._1)) //将(pid,value)转成(pName,value)
        rdd.foreachPartition(records => {
          outputFun(records, time, MAccTradePricePerProduct)
        })
      })

    // 客群分析
    val cl = customerList.value
    tradeDStream.map(trade => (if (cl.contains(trade.userId)) "老客" else "新客", 1d))
      .mapWithState(StateSpec.function(mappingFun))
      .foreachRDD((rdd, time) => {
        rdd.foreachPartition(outputFun(_, time, MAccTradeCntPerUserType))
      })
  }
}