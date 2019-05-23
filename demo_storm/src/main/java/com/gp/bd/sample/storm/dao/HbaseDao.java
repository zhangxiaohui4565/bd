package com.gp.bd.sample.storm.dao;



import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import java.util.List;


public interface HbaseDao {
    public void save(Put put,String tableName);
    public void save(List<Put> put, String tableName);
    public void insert(String tableName,String rowKey,String family,String quailifer,String value);
    public void insert(String tableName,String rowKey,String family,String quailifer[],String value[]);
    public Result getOneRow(String tableName,String rowKey);
    public List<Result> getRows(String tableName,String rowKey_like);
    public List<Result> getRows(String tableName, String rowKey_like,String cols[]);
}
