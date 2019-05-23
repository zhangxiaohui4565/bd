package com.gupao.bd.kudu.demo;


import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;

import java.util.ArrayList;
import java.util.List;

/*
*
*  通过Java Native API展示create table,insert,select,drop table等操作
*
* */
public class NativeAPIExample {

    private static final String KUDU_MASTER = System.getProperty("kudu.master", "quickstart.cloudera");

    public static void main(String[] args) {

        System.out.println("-----------------------------------------------");
        System.out.println("Will try to connect to Kudu master at " + KUDU_MASTER);
        System.out.println("Run with -Dkudu.master=myHost:port to override.");
        System.out.println("-----------------------------------------------");
        String tableName = "java_demo_" + System.currentTimeMillis();

        //1、创建KuduClient
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).defaultAdminOperationTimeoutMs(60000).build();

        try {

            //2、Create Table

            //2.1、设置schema
            List<ColumnSchema> columns = new ArrayList(2);
            columns.add(new ColumnSchema.ColumnSchemaBuilder("id", Type.INT32)
                    .key(true)
                    .build());
            columns.add(new ColumnSchema.ColumnSchemaBuilder("name", Type.STRING)
                    .build());
            Schema schema = new Schema(columns);

            //2.2、设置partition column
            List<String> rangeKeys = new ArrayList<>();
            rangeKeys.add("id");

            //2.3、执行DDL
            client.createTable(tableName, schema,
                    new CreateTableOptions().addHashPartitions(rangeKeys, 4).setNumReplicas(1));

            KuduTable table = client.openTable(tableName);
            KuduSession session = client.newSession();
            session.setTimeoutMillis(60000);

            //3、insert
            for (int i = 0; i < 3; i++) {
                Insert insert = table.newInsert();
                PartialRow row = insert.getRow();
                row.addInt(0, i);
                row.addString(1, "name_" + i);
                session.apply(insert);
            }

            //4、select(scan)
            List<String> projectColumns = new ArrayList<>(2);
            projectColumns.add("id");
            projectColumns.add("name");
            KuduScanner scanner = client.newScannerBuilder(table)
                    .setProjectedColumnNames(projectColumns)
                    .build();
            System.out.println("---------------------------------");
            while (scanner.hasMoreRows()) {
                RowResultIterator results = scanner.nextRows();
                while (results.hasNext()) {
                    RowResult result = results.next();
                    System.out.println(result.getInt(0) + "," + result.getString(1));
                }
            }
            System.out.println("---------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(60 * 1000);
                //5、drop table
//                client.deleteTable(tableName);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    client.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
