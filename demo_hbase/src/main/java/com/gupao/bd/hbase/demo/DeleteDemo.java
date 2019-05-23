package com.gupao.bd.hbase.demo;


import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * 删除记录
 */
public class DeleteDemo extends BaseDemo {

    public static void main(String[] args) throws IOException {

        Table client = hBaseClient.getTable(TABLE_NAME);

//        deleteCol(client);
//        deleteRow(client);
        putAfterDel(client);
        client.close();
    }

    /**
     * 删除整行
     */
    private static void deleteRow(Table table) throws IOException {
        Delete delete = new Delete("D00001".getBytes());
        table.delete(delete);
    }

    /**
     * 删除单列
     */
    private static void deleteCol(Table table) throws IOException {
        Delete delete = new Delete("D00001".getBytes());
        delete.addColumn(CF, COL_AGE);
        table.delete(delete);
    }

    /**
     * 删除之后，老version的数据无法插入，需要进行一次compact;新version的数据可以插入
     * <p>
     * flush 'gp:test'
     * major_compact 'gp:test'
     */
    private static void putAfterDel(Table table) throws IOException {
//        Put put = new Put("D00001".getBytes(), 1L);
        Put put = new Put("D00001".getBytes(),Long.MAX_VALUE);
        put.addColumn(CF, COL_NAME, "Tom".getBytes());
        table.put(put);
    }
}
