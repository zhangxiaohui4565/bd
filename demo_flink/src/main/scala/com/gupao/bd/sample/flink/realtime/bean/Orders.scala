package com.gupao.bd.sample.flink.realtime.bean

import java.sql.Timestamp

case class Orders(orderId:Int, productName:String, productNum:Int,amount:Double,isPay:Int,categoryId:Int,orderTime:Timestamp)