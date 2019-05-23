package com.gupao.bd.sample.flink.realtime.sql.udf

import java.lang.{Long => JLong}

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.tuple.{Tuple2 => JTuple2}
import org.apache.flink.api.java.typeutils.TupleTypeInfo
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.{TableEnvironment, Types}
import org.apache.flink.table.functions.AggregateFunction
import org.apache.flink.types.Row
import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala._

class Max extends AggregateFunction[JLong, MaxMinAccum] {


  /**
    * 初始化AggregateFunction的accumulator，
    * 系统在第一个做aggregate计算之前调用一次这个方法
    *
    * @return
    */
  override def createAccumulator(): MaxMinAccum = {
    new MaxMinAccum
  }

  /**
    * 系统在每次aggregate计算完成后调用这个方法
    *
    * @param acc
    * @return
    */
  override def getValue(acc: MaxMinAccum): JLong = {
    if (acc.max == 0) {
      null
    } else {
     acc.max
    }
  }

  /**
    *
    * 您需要实现一个accumulate方法，来描述如何计算用户的输入的数据，并更新到accumulator中。
    * accumulate方法的第一个参数必须是使用AggregateFunction的ACC类型的accumulator。在系统运行
    * 过程中，底层runtime代码会把历史状态accumulator，和您指定的上游数据（支持任意数量，任意
    * 类型的数据）做为参数一起发送给accumulate计算。
    **/

  def accumulate(acc: MaxMinAccum, iValue: JLong, iWeight: JLong): Unit = {
    if(iValue> acc.max){
      acc.max = iValue
    }
  }

  /**
    *
    * 在实时计算的场景里，很多时候的计算都是对无限流的一个提前的观测值（early firing）。既然有
    * early firing，就会有对发出的结果的修改，这个操作叫做撤回（retract），SQL翻译优化器会帮助
    * 您自动判断哪些情况下会产生撤回的数据，哪些操作需要处理带有撤回标记的数据。如何处理撤回
    * 的数据，需要用户自己实现一个retract方法。retract方法是accumulate方法的逆操作。例如count
    * UDAF，在accumulate的时候每来一条数据要加一，在retract的时候就是要减一。类似于accumulate
    * 方法，retract方法的第一个参数必须是使用AggregateFunction的ACC类型的accumulator。在系统
    * 运行过程中，底层runtime代码会把历史状态accumulator，和用户指定的上游数据（支持任意数量，
    * 任意类型的数据）一起发送给retract计算。
    *
    **/

  def retract(acc: MaxMinAccum, iValue: JLong, iWeight: JLong): Unit = {

  }

  /**
    * merge方法在批计算中被广泛的用到，在实时计算中也有些场景需要merge，例如sesession window。
    * 由于实时计算具有out of order的特性，后到的数据往往有可能位于两个原本分开的session中间，这
    * 样就把两个session合为一个session。这种场景出现的时候，我们就需要一个merge方法把几个
    * accumulator合为一个accumulator。merge方法的第一个参数必须是使用AggregateFunction的ACC
    * 类型的accumulator，而且这第一个accumulator是merge方法完成之后状态所存放的地方。merge方法
    * 的第二个参数是一个ACC type的accumulator遍历迭代器，里面有可能有一个或者多个accumulator。
    **/
  def merge(acc: MaxMinAccum, it: java.lang.Iterable[MaxMinAccum]): Unit = {
  }

  def resetAccumulator(acc: MaxMinAccum): Unit = {
    acc.max = 0L
    acc.min= 0L
  }

  override def getAccumulatorType: TypeInformation[MaxMinAccum] = {
    new TupleTypeInfo(classOf[MaxMinAccum], Types.LONG, Types.LONG)
  }

  override def getResultType: TypeInformation[JLong] = Types.LONG
}


class MaxMinAccum extends JTuple2[JLong, JLong] {
  var min = 0L
  var max = 0L
}


case class MaxData(id:Long,numbers:String)

object MaxTest{
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tEnv = TableEnvironment.getTableEnvironment(env)
    val data1 = Seq(
      MaxData(1001,"11030,1031,1032,1033,1190"),
      MaxData(1002,"2030,2031,2032,2033,2190"),
      MaxData(1005,"2030,2031,2032,2033,2190"),
      MaxData(10010,"2030,2031,2032,2033,2190"))

    val maxDs:DataStream[MaxData]  = env.fromCollection[MaxData](data1)

    tEnv.registerDataStream("maxTable",maxDs,'id,'numbers)

    tEnv.registerFunction("max", new Max())

    val sql =
      """
        |SELECT
        |  max(id)
        |  FROM maxTable
      """.stripMargin

    val result = tEnv.sqlQuery(sql)

    result.toRetractStream[Row].print()

    env.execute()

  }
}