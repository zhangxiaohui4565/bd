package com.gupao.bd.hbase.demo;


import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * DDL操作
 */
public class AdminDemo extends BaseDemo {

    public static void main(String[] args) throws IOException {

        Admin admin = hBaseClient.getAdmin();
        initNamespace(admin);
//        flushAndCompact(admin);
        admin.close();

    }

    private static void flushAndCompact(Admin admin) throws IOException {
        admin.flush(TABLE_NAME);
        admin.majorCompact(TABLE_NAME);
    }

    private static void initNamespace(Admin admin) throws IOException {
        //hbase api没有类似existNamespace的方法，故以下变通的方法来判断某个namespace是否存在
        Set<String> existsNS = new HashSet<>();
        for (NamespaceDescriptor nd : admin.listNamespaceDescriptors()) {
            existsNS.add(nd.getName());
        }

        //删除已经存在的NS
        if (existsNS.contains(NS)) {
            System.out.println(NS + " exists,delete it first ...");

            //列出hbase中所有的表,逐个删除
            for (TableName tn : admin.listTableNamesByNamespace(NS)) {

                //下线表
                System.out.println("disable table:" + tn.getNameWithNamespaceInclAsString());
                admin.disableTable(tn);

                //删除表
                System.out.println("delete table:" + tn.getNameWithNamespaceInclAsString());
                admin.deleteTable(tn);
            }

            //删除namespace
            admin.deleteNamespace(NS);

            System.out.println("delete ns:" + NS);
        }

        //创建ns
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(NS).build();
        admin.createNamespace(namespaceDescriptor);
        System.out.println("create ns:" + NS);


        //创建table
        HTableDescriptor descriptor = new HTableDescriptor(TABLE_NAME);

        //列族
        HColumnDescriptor cf = new HColumnDescriptor("info");
        cf.setTimeToLive(HConstants.FOREVER);
        cf.setMinVersions(1);
        cf.setMaxVersions(3);
        descriptor.addFamily(cf);

        admin.createTable(descriptor);

        System.out.println("create table:" + TABLE_NAME);

        System.out.println("desc table:" + admin.getTableDescriptor(TABLE_NAME));
    }
}
