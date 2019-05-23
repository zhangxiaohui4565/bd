package com.gupao.bd.hbase.demo;


import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据RowKey查询
 */
public class GetDemo extends BaseDemo {

    public static void main(String[] args) throws IOException {
        Table client = hBaseClient.getTable(TABLE_NAME);

        singleGet(client);

        client.close();
    }

    /**
     * 单条获取
     */
    public static void singleGet(Table table) throws IOException {
        Get get = new Get("D00001".getBytes());
        Result result = table.get(get);
        showStudentInfo(result);
    }

    /**
     * 批量获取
     */
    public static void batchGet(Table table) throws IOException {
        Get get = new Get("D00002".getBytes());
        Get get2 = new Get("D00003".getBytes());

        List<Get> gets = new ArrayList();
        gets.add(get);
        gets.add(get2);

        Result[] results = table.get(gets);

        for (Result r : results) {
            showStudentInfo(r);
        }
    }
}
