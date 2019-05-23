package com.gupao.bd.sample.flink.realtime.sink

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

import scala.collection.mutable

class RetractSink extends RichSinkFunction[(Boolean,(String,Long))]{
  private var resultSet:mutable.Set[(String,Long)] =_

  override def open(parameters: Configuration): Unit = {
    resultSet = new mutable.HashSet[(String, Long)]()
  }


  override def invoke(value: (Boolean, (String, Long)), context: SinkFunction.Context[_]): Unit = {
    if(value._1){
      //添加数据
      resultSet.add(value._2)
    }else{
      // 撤回数据
      resultSet.remove(value._2);
    }
  }





}
