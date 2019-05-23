package com.gupao.bd.sample.flink.realtime.bean

case class Order(user:Int,product:String,amount:Int,rowtime:Long)
