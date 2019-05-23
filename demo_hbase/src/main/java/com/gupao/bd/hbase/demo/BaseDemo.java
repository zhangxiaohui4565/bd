package com.gupao.bd.hbase.demo;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * 创建HBaseClient(admin/table)
 */
public abstract class BaseDemo {

    protected static HBaseClient hBaseClient = new HBaseClient("localhost:2383");

    protected static final String NS = "gupao";

    //id,name,sex,age,enter_date,status
    protected static final String TBL = "students";

    protected static final TableName TABLE_NAME = TableName.valueOf(NS, TBL);

    protected static final byte[] CF = "info".getBytes();

    protected static final byte[] COL_NAME = "name".getBytes();
    protected static final byte[] COL_GENDER = "gender".getBytes();
    protected static final byte[] COL_AGE = "age".getBytes();
    protected static final byte[] COL_ENTER_DATE = "enter_date".getBytes();
    protected static final byte[] COL_STATUS = "status".getBytes();

    protected static void showStudentInfo(Result result) {

        System.out.print("\nid:" + Bytes.toString(result.getRow()));
        System.out.print(",name:" + Bytes.toString(result.getValue(CF, COL_NAME)));
        System.out.print(",gender:" + Bytes.toString(result.getValue(CF, COL_GENDER)));
        System.out.print(",age:" + Bytes.toString(result.getValue(CF, COL_AGE)));
        System.out.print(",enter_date:" + Bytes.toString(result.getValue(CF, COL_ENTER_DATE)));
        System.out.print(",status:" + Bytes.toString(result.getValue(CF, COL_STATUS))+"\n");

    }
}
