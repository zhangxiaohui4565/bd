package com.gupao.bd.sample.flink.realtime.bean

case  class CountWithTs(key:String,count:Long,lastModified:Long)