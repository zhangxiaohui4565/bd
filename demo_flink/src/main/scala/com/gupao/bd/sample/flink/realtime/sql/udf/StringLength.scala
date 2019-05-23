package com.gupao.bd.sample.flink.realtime.sql.udf

import org.apache.flink.table.functions.ScalarFunction


/**
  * 自定义字符串长度函数
  */
class StringLength extends  ScalarFunction{

  def eval(s: String): Long = {
    if(s.isEmpty){
      0
    }else{
      s.length
    }
  }

  def eval(s1:String,s2:String):Long = eval(s1) + eval(s2)

}
