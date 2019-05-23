package com.gupao.bd.trademonitor.mcs.dao

import java.util.{ArrayList => jArrayList}

import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}

/**
  * 功能：从HBase加载/写入数据
  **/
class HBaseClient(zkQuorum: String) {

  val conf = HBaseConfiguration.create()
  conf.set("hbase.zookeeper.quorum", zkQuorum)

  val conn: Connection = ConnectionFactory.createConnection(conf)

  val COL_FAMILY = "info".getBytes

  def get(tableName: String, rowkey: String): Result = {
    val table = conn.getTable(TableName.valueOf(tableName))
    val get: Get = new Get(rowkey.getBytes)
    val result: Result = table.get(get)
    result
  }
}