package com.gp.bd.sample.storm.dao;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HbaseDaoImpl implements HbaseDao{

    private String CF = "info";
    Connection connection = null;
    public HbaseDaoImpl() {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "localhost:2181");
        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Put put, String tableName) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insert(String tableName, String rowKey, String family,
                       String quailifer, String value) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(rowKey.getBytes());
            put.addColumn(family.getBytes(), quailifer.getBytes(), value.getBytes());
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void save(List<Put> puts, String tableName) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            table.put(puts);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Result getOneRow(String tableName, String rowKey) {
        Table table = null;
        Result result = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            result= table.get(get);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<Result> getRows(String tableName, String rowKey_like) {
        Table table = null;
        List<Result> result = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            PrefixFilter filter = new PrefixFilter(rowKey_like.getBytes());
            Scan scan = new Scan();
            scan.setFilter(filter);
            ResultScanner scanner =table.getScanner(scan);
            result = new ArrayList<Result>();
            for (Result rs : scanner) {
                result.add(rs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }



    @Override
    public List<Result> getRows(String tableName, String rowKey_like,String cols[]) {
        Table table = null;
        List<Result> result = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            PrefixFilter filter = new PrefixFilter(rowKey_like.getBytes());
            Scan scan = new Scan();
            scan.setFilter(filter);
            for (int i=0;i<cols.length;i++) {
                scan.addColumn(CF.getBytes(), cols[i].getBytes());
            }
            ResultScanner scanner =table.getScanner(scan);
            result = new ArrayList<Result>();
            for (Result rs : scanner) {
                result.add(rs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Override
    public void insert(String tableName, String rowKey, String family, String[] quailifer, String[] value) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(rowKey.getBytes());
            for (int i = 0; i < quailifer.length; i++) {
                String col = quailifer[i];
                String val = value[i];
                put.addColumn(family.getBytes(), col.getBytes(), val.getBytes());
            }
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}