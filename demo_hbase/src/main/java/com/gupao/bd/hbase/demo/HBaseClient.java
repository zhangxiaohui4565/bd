package com.gupao.bd.hbase.demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;

/**
 * 通过ZK连接HBase，创建HBaseClient
 */
public class HBaseClient {

//    在HBase用户中，常见的使用Connection的错误方法有：
//   （1）自己实现一个Connection对象的资源池，每次使用都从资源池中取出一个Connection对象；
//   （2）每个线程一个Connection对象。
//   （3）每次访问HBase的时候临时创建一个Connection对象，使用完之后调用close关闭连接。
//    在HBase中Connection类已经实现了对连接的管理功能，所以我们不需要自己在Connection之上再做额外的管理。
//    另外，Connection是线程安全的，而Table和Admin则不是线程安全的，
//    因此正确的做法是一个进程共用一个Connection对象，而在不同的线程中使用单独的Table和Admin对象
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

    /**
     * 负责DDL
     */
    public Admin getAdmin() {
        try {
            return conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 负责DML
     */
    public Table getTable(TableName tableName) {
        try {
            return conn.getTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
