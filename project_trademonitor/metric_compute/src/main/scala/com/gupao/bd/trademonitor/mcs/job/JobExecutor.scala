package com.gupao.bd.trademonitor.mcs.job

import java.util.concurrent.{Executors, ExecutorService}

import com.gupao.bd.trademonitor.mcs.job.spark.StreamingMetricComputeJob
import org.apache.commons.lang3.Validate


object JobExecutor {

  def main(args: Array[String]): Unit = {
    Validate.isTrue(args != null && !args.isEmpty, "需要通过参数指定配置文件的全路径，配置文件中包含kafka/spark/hbase相关的信息")

    val jobConf: JobConf = new JobConf(args(0))
    val pool: ExecutorService = Executors.newFixedThreadPool(1)

    jobConf.ENGINE_NAME match {
      case "spark_streaming" => pool.execute(new StreamingMetricComputeJob(jobConf))
      //待扩展,增加其他实时计算引擎
      case _ => throw new RuntimeException("unsupported job engine")
    }
  }
}
