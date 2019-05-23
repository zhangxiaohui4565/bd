package com.gupao.bd.hbase.demo;


import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * insert/update
 */
public class PutDemo extends BaseDemo {

    public static void main(String[] args) throws IOException {
        Table table = hBaseClient.getTable(TABLE_NAME);
//        singlePut(client);
        batchPut(table);
        table.close();
    }


    /**
     * 单条插入
     */
    public static void singlePut(Table table) throws IOException {
        Put put = new Put("D00001".getBytes());
        put.addColumn(CF, COL_NAME, "Tom".getBytes());
        put.addColumn(CF, COL_AGE, "25".getBytes());
        put.addColumn(CF, COL_GENDER, "F".getBytes());
        put.addColumn(CF, COL_ENTER_DATE, "2018-10-01".getBytes());
        put.addColumn(CF, COL_STATUS, "learning".getBytes());
        table.put(put);
    }

    /**
     * 批量插入
     */
    public static void batchPut(Table table) throws IOException {
        //年龄未知，不填写
        Put put1 = new Put("D00002".getBytes()); // 1 - Long.MAX_VALUE,如设置为Long.MAX_VALUE，HBase会修改成系统当前时间，精确到毫秒
        put1.addColumn(CF, COL_NAME, "小明".getBytes());
        put1.addColumn(CF, COL_GENDER, "M".getBytes());
        put1.addColumn(CF, COL_ENTER_DATE, "2019-01-01".getBytes());
        put1.addColumn(CF, COL_STATUS, "learning".getBytes());


        Put put2 = new Put("D00003".getBytes());
        put2.addColumn(CF, COL_NAME, "大强".getBytes());
        put2.addColumn(CF, COL_AGE, "30".getBytes());
        put2.addColumn(CF, COL_GENDER, "M".getBytes());
        put2.addColumn(CF, COL_ENTER_DATE, "2018-01-01".getBytes());
        put2.addColumn(CF, COL_STATUS, "graduated".getBytes());

        List<Put> puts = new ArrayList();
        puts.add(put1);
        puts.add(put2);

        table.put(puts);
    }
}
