package com.gupao.bd.trademonitor.dcs.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.Map;

/**
 * 功能：实现HBase的crud操作
 */
public class HBaseClient {

    private Connection conn;

    public HBaseClient(String zkQuorum) {
        Configuration defaultConf = HBaseConfiguration.create();
        defaultConf.set("hbase.zookeeper.quorum", zkQuorum);

        try {
            conn = ConnectionFactory.createConnection(defaultConf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String tableName, String rowkey, JSONObject record) {
        try {
            Table table = conn.getTable(TableName.valueOf(tableName));

            Put put = new Put(rowkey.getBytes());

            for (Map.Entry<String, Object> entry : record.entrySet()) {
                put.addColumn("info".getBytes(), entry.getKey().getBytes(), entry.getValue().toString().getBytes());
            }

            table.put(put);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
