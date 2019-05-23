package com.gupao.bd.hbase.demo;


import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.*;

import java.io.IOException;

/**
 * 范围扫描及过滤器使用
 */
public class ScanDemo extends BaseDemo {

    public static void main(String[] args) throws IOException {

        Table client = hBaseClient.getTable(TABLE_NAME);

        Scan scan = new Scan();

        //include
        scan.setStartRow("D00001".getBytes());

        //exclude
        scan.setStopRow("D00004".getBytes());

        ResultScanner rs = client.getScanner(scan);
        showResults(rs, "rs:D00001 - D00003");

        Filter f1 = new SingleColumnValueFilter(CF, COL_STATUS, CompareFilter.CompareOp.EQUAL, new BinaryComparator("learning".getBytes()));
        scan.setFilter(f1);

        ResultScanner rs2 = client.getScanner(scan);
        showResults(rs2, "rs2:状态为learning");


        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        Filter f2 = new SingleColumnValueFilter(CF, COL_GENDER, CompareFilter.CompareOp.EQUAL, new BinaryComparator("F".getBytes()));
        filterList.addFilter(f1);
        filterList.addFilter(f2);
        scan.setFilter(filterList);

        ResultScanner rs3 = client.getScanner(scan);
        showResults(rs3, "rs3:状态为learning且性别是F");

        client.close();
    }

    private static void showResults(ResultScanner rs, String tag) {
        System.out.println("\n---- " + tag + " ----------");
        for (Result r : rs) {
            showStudentInfo(r);
        }
    }
}
