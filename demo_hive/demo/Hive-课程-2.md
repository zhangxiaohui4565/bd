# Hive课程-下

## 演示1 - 分区表

### 步骤

1. 创建分区表
```sql
DROP TABLE IF EXISTS mw_partitioned_access_logs;
CREATE TABLE mw_partitioned_access_logs (
    ip STRING,
    request_time STRING,
    method STRING,
    url STRING,
    http_version STRING,
    code1 STRING,
    code2 STRING,
    dash STRING,
    user_agent STRING,
    timestamp int)
PARTITIONED BY (request_date STRING)
STORED AS PARQUET
;
```

2. 将日志表写入分区表，使用动态分区插入

```sql
set hive.exec.dynamic.partition.mode=nonstrict;

INSERT OVERWRITE TABLE mw_partitioned_access_logs 
PARTITION (request_date)
SELECT ip, request_time, method, url, http_version, code1, code2, dash, user_agent, timestamp, to_date(request_time) as request_date
FROM mw_tokenized_access_logs
;
```

3. 观察分区表目录结构
```bash
hdfs dfs -ls /user/hive/warehouse/mw_partitioned_access_logs
```

## 演示二 - 分桶表

### 步骤

1. 创建日志分桶表
    按IP的第一段分桶，然后按请求时间排序

```sql
DROP TABLE IF EXISTS mw_bucketed_access_logs;
CREATE TABLE mw_bucketed_access_logs (
    first_ip_addr INT,
    request_time STRING)
CLUSTERED BY (first_ip_addr) SORTED BY (request_time) INTO 10 BUCKETS
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
;
set hive.enforce.sorting=true;
SET hive.enforce.bucketing = true;
INSERT OVERWRITE TABLE mw_bucketed_access_logs 
SELECT cast(split(ip, '\\.')[0] as int) as first_ip_addr, request_time
FROM mw_tokenized_access_logs
DISTRIBUTE BY first_ip_addr
SORT BY request_time
;
```

2. 观察分桶表的物理存储结构
```bash
hdfs dfs -ls /user/hive/warehouse/mw_bucketed_access_logs/
hdfs dfs -cat /user/hive/warehouse/mw_bucketed_access_logs/000000_0 | less
```

## 演示三 - SMB Join

### 步骤

1. 用户表关联订单表，统计男女用户下单的比例
```sql
CREATE EXTERNAL TABLE mw_source_users (
    user_id INT,
    age int,
    gender string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
LOCATION '/user/hive/warehouse/mw_source_users'
;

DROP TABLE IF EXISTS mw_bucketed_users;
CREATE TABLE mw_bucketed_users (
    user_id INT,
    age int,
    gender string)
CLUSTERED BY (user_id) SORTED BY (user_id) INTO 10 BUCKETS
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
;

SET hive.enforce.bucketing = true;
INSERT OVERWRITE TABLE mw_bucketed_users
select * from mw_source_users
DISTRIBUTE BY user_id
SORT By user_id
;

CREATE EXTERNAL TABLE mw_source_orders (
    user_id INT,
    price decimal(18,2)
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
LOCATION '/user/hive/warehouse/mw_source_orders'
;

DROP TABLE IF EXISTS mw_bucketed_orders;
CREATE TABLE mw_bucketed_orders (
    user_id INT,
    price decimal(18,2))
CLUSTERED BY (user_id) SORTED BY (user_id) INTO 10 BUCKETS
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
;

INSERT OVERWRITE TABLE mw_bucketed_orders
select * from mw_source_orders
DISTRIBUTE BY user_id
SORT By user_id
;
```

2. 观察执行计划
```sql
set hive.auto.convert.sortmerge.join=true;
set hive.optimize.bucketmapjoin=true;
set hive.optimize.bucketmapjoin.sortedmerge=true;

explain
select A.user_id, sum(B.price)
from mw_bucketed_users A
inner join mw_bucketed_orders B
        on (A.user_id = B.user_id)
group by A.user_id
;
```

## 演示四 - Parquet表的压缩

1. 新建一张访问日志的Parquet表，插入数据时启用压缩
```sql
DROP TABLE IF EXISTS mw_compressed_access_logs;
CREATE TABLE mw_compressed_access_logs (
    ip STRING,
    request_time STRING,
    method STRING,
    url STRING,
    http_version STRING,
    code1 STRING,
    code2 STRING,
    dash STRING,
    user_agent STRING,
    timestamp int)
STORED AS PARQUET TBLPROPERTIES ("parquet.compression"="SNAPPY");

SET hive.exec.compress.intermediate=true;
SET mapreduce.map.output.compress=true;

INSERT OVERWRITE TABLE mw_compressed_access_logs
SELECT * FROM mw_tokenized_access_logs;

describe formatted mw_compressed_access_logs;
```

2. 和原来不启用压缩的Parquet表进行比对

大小

```
hdfs dfs -ls /user/hive/warehouse/mw_tokenized_access_logs/
```
Parquet压缩前: 4158559

```
hdfs dfs -ls /user/hive/warehouse/mw_compressed_access_logs/
```

Parquet压缩后: 1604620
