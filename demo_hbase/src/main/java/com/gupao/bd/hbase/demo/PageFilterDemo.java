package com.gupao.bd.hbase.demo;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * PageFilter的用法
 */
public class PageFilterDemo extends BaseDemo {
    public static void main(String[] args) throws IOException {

        long pageSize = 10;
        int totalRowsCount = 0;
        PageFilter filter = new PageFilter(pageSize);
        byte[] lastRow = null;
        while (true) {
            Scan scan = new Scan();
            scan.setFilter(filter);
            if (lastRow != null) {
                byte[] postfix = Bytes.toBytes("postfix");
                byte[] startRow = Bytes.add(lastRow, postfix);
                scan.setStartRow(startRow);
                System.out.println("start row : " + Bytes.toString(startRow));
            }

            Table _hTable = hBaseClient.getTable(TABLE_NAME);

            ResultScanner scanner = _hTable.getScanner(scan);
            int localRowsCount = 0;
            for (Result result : scanner) {
                System.out.println(localRowsCount++ + " : " + result);
                totalRowsCount++;
                // ResultScanner 的结果集是排序好的，这样就能够取到最后一个row了
                lastRow = result.getRow();
            }
            scanner.close();

            if (localRowsCount == 0) break;
        }
        System.out.println("total rows is : " + totalRowsCount);
    }
}
