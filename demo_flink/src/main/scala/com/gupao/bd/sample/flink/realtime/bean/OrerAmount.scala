package com.gupao.bd.sample.flink.realtime.bean

import java.sql.Timestamp

case class OrderAmount(dt:Timestamp,categoryId:Int,orderNum:Long,orderAmoumt:Double)
