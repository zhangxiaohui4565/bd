package com.gupao.bd.trademonitor.mcs.model

import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes

/**
  * 对应product表中的数据
  **/
case class HProduct(hbaseRecord: Result) {

  // 产品ID
  val id = Bytes.toString(hbaseRecord.getRow)

  // 产品名称
  val pName = Bytes.toString(hbaseRecord.getValue("info".getBytes(), "p_name".getBytes()))

  // 产品类别
  val pCategory = Bytes.toString(hbaseRecord.getValue("info".getBytes(), "p_category".getBytes()))
}

