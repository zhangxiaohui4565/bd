package com.gupao.bd.sample.flink.realtime.sql

import java.sql.Timestamp

import com.gupao.bd.sample.flink.realtime.bean.SensorInfo
import com.gupao.bd.sample.flink.realtime.sql.udf.{Add, Split}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.table.api.scala._
import org.apache.flink.table.api.{TableEnvironment, Types}

/**
  * 字段：sensorId,speed,ts
  * 1001,50,1
  * 1002,
  */
object SQLTest {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val maxDelay = 100

    env.getConfig.disableClosureCleaner()

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.setParallelism(1)


    val tableEnv = TableEnvironment.getTableEnvironment(env)

    tableEnv.registerFunction("add", new Add(3))
    tableEnv.registerFunction("split", new Split("-"))


    val fieldNames = Array("a", "b", "c")
    val fieldTypes = Array(Types.INT, Types.STRING, Types.LONG)

    val ds = env.socketTextStream("127.0.0.1", 7777)
      .map(k => {
        val tokens = k.split(",")
        SensorInfo(tokens(0).toInt, tokens(1).toLong, tokens(2).toLong,tokens(3))
      })
      .assignTimestampsAndWatermarks(new TimestampExtractor(maxDelay))

    //  .assignAscendingTimestamps(_.ts)

    tableEnv.registerDataStream("sensors", ds, 'sensorId, 'speed,'ext, 'ts.rowtime)

    val sql =
      """
        |SELECT
        |  add(sensorId),
        |  SUM(speed) AS avgSpeed,
        |  TUMBLE_START(ts, INTERVAL '1' SECOND),
        |  TUMBLE_ROWTIME(ts, INTERVAL '1' SECOND)
        |  FROM sensors
        | GROUP BY TUMBLE(ts,INTERVAL '1' SECOND), sensorId
      """.stripMargin

    val sql2 =
      """
        |SELECT
        |  ext,
        |  word,
        |  length
        |  FROM sensors, LATERAL TABLE(split(ext)) as T(word,length)
      """.stripMargin

    val sql3 =
      """
        |SELECT
        |  add(sensorId),
        |  SUM(speed) AS avgSpeed
        |  FROM sensors
        | GROUP BY  sensorId
      """.stripMargin

    val result = tableEnv.sqlQuery(sql)

   // println(tableEnv.explain(result))



    result.toRetractStream[RetractAvgSpeed].print()


    //result.toRetractStream[AvgSpeed]

//    result.toAppendStream[SplitTest].print()
    env.execute(this.getClass.getSimpleName)

  }

  case class SplitTest(ext: String, word: String, length:Int)

  case class AvgSpeed(sensorId: Int, avgSpeed: Long, time: Timestamp)

  case class RetractAvgSpeed(sensorId: Int, totalSpeed: Long,starTime: Timestamp,endTime:Timestamp)


  //基于water发射器发送watermark
  class TimestampExtractor(maxDelay: Int) extends AssignerWithPeriodicWatermarks[SensorInfo] with Serializable {
    var maxTs = 0L

    override def extractTimestamp(e: SensorInfo, prevElementTimestamp: Long) = {
      val ts = e.ts
      maxTs = Math.max(ts, maxTs)
      ts
    }

    override def getCurrentWatermark(): Watermark = {
      new Watermark(maxTs - maxDelay)
    }

  }


}


