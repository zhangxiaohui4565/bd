package com.gupao.bd.sample.flink.realtime.sql.udf


import java.lang.{Integer => JInteger, Long => JLong}

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.tuple.{Tuple2 => JTuple2}
import org.apache.flink.api.java.typeutils.TupleTypeInfo
import org.apache.flink.table.api.Types
import org.apache.flink.table.functions.AggregateFunction


class WeightedAvg extends AggregateFunction[JLong, WeightedAvgAccum] {


  /**
    * 初始化AggregateFunction的accumulator，
    * 系统在第一个做aggregate计算之前调用一次这个方法
    *
    * @return
    */
  override def createAccumulator(): WeightedAvgAccum = {
    new WeightedAvgAccum
  }

  /**
    * 系统在每次aggregate计算完成后调用这个方法
    *
    * @param acc
    * @return
    */
  override def getValue(acc: WeightedAvgAccum): JLong = {
    if (acc.count == 0) {
      null
    } else {
      acc.sum / acc.count
    }
  }

  /**
    *
    * 您需要实现一个accumulate方法，来描述如何计算用户的输入的数据，并更新到accumulator中。
    * accumulate方法的第一个参数必须是使用AggregateFunction的ACC类型的accumulator。在系统运行
    * 过程中，底层runtime代码会把历史状态accumulator，和您指定的上游数据（支持任意数量，任意
    * 类型的数据）做为参数一起发送给accumulate计算。
    **/

  def accumulate(acc: WeightedAvgAccum, iValue: JLong, iWeight: JInteger): Unit = {
    acc.sum += iValue * iWeight
    acc.count += iWeight
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

  def retract(acc: WeightedAvgAccum, iValue: JLong, iWeight: JInteger): Unit = {
    acc.sum -= iValue * iWeight
    acc.count -= iWeight
  }

  /**
    * merge方法在批计算中被广泛的用到，在实时计算中也有些场景需要merge，例如sesession window。
    * 由于实时计算具有out of order的特性，后到的数据往往有可能位于两个原本分开的session中间，这
    * 样就把两个session合为一个session。这种场景出现的时候，我们就需要一个merge方法把几个
    * accumulator合为一个accumulator。merge方法的第一个参数必须是使用AggregateFunction的ACC
    * 类型的accumulator，而且这第一个accumulator是merge方法完成之后状态所存放的地方。merge方法
    * 的第二个参数是一个ACC type的accumulator遍历迭代器，里面有可能有一个或者多个accumulator。
    **/
  def merge(acc: WeightedAvgAccum, it: java.lang.Iterable[WeightedAvgAccum]): Unit = {
    val iter = it.iterator()
    while (iter.hasNext) {
      val a = iter.next()
      acc.count += a.count
      acc.sum += a.sum
    }
  }

  def resetAccumulator(acc: WeightedAvgAccum): Unit = {
    acc.count = 0
    acc.sum = 0L
  }

  override def getAccumulatorType: TypeInformation[WeightedAvgAccum] = {
    new TupleTypeInfo(classOf[WeightedAvgAccum], Types.LONG, Types.INT)
  }

  override def getResultType: TypeInformation[JLong] = Types.LONG
}


class WeightedAvgAccum extends JTuple2[JLong, JInteger] {
  var sum = 0L
  var count = 0
}