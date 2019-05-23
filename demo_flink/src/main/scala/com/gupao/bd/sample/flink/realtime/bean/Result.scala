package com.gupao.bd.sample.flink.realtime.bean

import java.sql.Timestamp

case class Result(user:Int,product:String,sum_amount:Int,window_start:Timestamp)
