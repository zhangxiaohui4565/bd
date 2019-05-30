# Hive课程-上

## 演示1

### 步骤

1. 将日志文件传到HDFS
```bash
sudo -u hdfs hadoop fs -mkdir /gp/hive/log

sudo -u hdfs hadoop fs -copyFromLocal /home/gupao/data/magicwind/access.log /gp/hive/log
```

检查文件是否已正确拷贝
```bash
hadoop fs -ls /gp/hive/log
```

2. 建立Hive外部表对应于日志文件
```sql
CREATE EXTERNAL TABLE access_log(
    ip STRING,
    request_time STRING,
    method STRING,
    url STRING,
    http_version STRING,
    code1 STRING,
    code2 STRING,
    dash STRING,
    user_agent STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
    'input.regex' = '([^ ]*) - - \\[([^\\]]*)\\] "([^\ ]*) ([^\ ]*) ([^\ ]*)" (\\d*) (\\d*) "([^"]*)" "([^"]*)"',
    'output.format.string' = "%1$$s %2$$s %3$$s %4$$s %5$$s %6$$s %7$$s %8$$s %9$$s")
LOCATION '/gp/hive/log';
```

3. 将TEXT表转换为PARQUET表
```sql
CREATE TABLE pa_access_log (
    ip STRING,
    request_time STRING,
    method STRING,
    url STRING,
    http_version STRING,
    code1 STRING,
    code2 STRING,
    dash STRING,
    user_agent STRING,
    `timestamp` int)
STORED AS PARQUET;


--LOCATION '/user/hive/warehouse/tokenized_access_logs_${date}';

INSERT OVERWRITE TABLE pa_access_log
SELECT 
  ip,
  from_unixtime(unix_timestamp(request_time, 'dd/MMM/yyyy:HH:mm:ss z'), 'yyyy-MM-dd HH:mm:ss z'),
  method,
  url,
  http_version,
  code1,
  code2,
  dash,
  user_agent,
  unix_timestamp(request_time, 'dd/MMM/yyyy:HH:mm:ss z')
FROM access_log;
```

注意观察Hive Job拆分成Map Reduce Job并执行

如何查看Hive Job执行的日志

4. 统计最多访问的5个IP
```sql
select ip, count(*) cnt
from mw_tokenized_access_logs_${date}
group by ip
order by cnt desc
limit 5
```

## 演示2

 1. 关联IP国家列表统计出访问最多的5个国家

观察ip_country_block表
```sql
create external table ip_country_block (
  network string,
  geoname_id int,
  registered_country_geoname_id int,
  represented_country_geoname_id int,
  is_anonymous_proxy boolean,
  is_satellite_provider boolean,
  first_ip_no bigint,
  last_ip_no bigint
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
location '/user/hive/warehouse/ip_country_block/'
;
```

观察ip_country_location表
```sql
create external table ip_country_location (
  geoname_id string,
  locale_code string,
  continent_code string,
  continent_name string,
  country_iso_code string,
  country_name string
)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
location '/user/hive/warehouse/ip_country_location/'
tblproperties ("skip.header.line.count"="1")
;
```

```sql
select 
    F.country_name, 
    count(*) ip_cnt, 
    sum(F.cnt) visits
from (
    select 
        A.ip, A.cnt, A.ip_to_int, C.country_name
    from (
        select
            ip,
            cnt,
            cast(regexp_extract(ip,"(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)",1) as bigint) * 16777216 +
            cast(regexp_extract(ip,"(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)",2) as bigint) * 65536 +
            cast(regexp_extract(ip,"(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)",3) as bigint) * 256 +
            cast(regexp_extract(ip,"(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)",4) as bigint) as ip_to_int,
            regexp_extract(ip,"(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)",1) first_no
        from (
            select ip, count(*) cnt
            from mw_tokenized_access_logs_${date}
            group by ip
        ) T
    ) A
    left join ip_country_block B 
           on (split(B.network, '\\.')[0] = first_no)
    left join ip_country_location C
           on (B.geoname_id = C.geoname_id)
    where A.ip_to_int between B.first_ip_no and B.last_ip_no
) F
group by F.country_name
order by visits desc
limit 5
```

2. 会话分析

建立会话表

```sql
DROP TABLE IF EXISTS mw_sessions;
CREATE TABLE mw_sessions (
    session_id STRING,
    session_number int,
    session_boundary int,
    request_time STRING,
    timestamp int,
    ip STRING,
    user_agent STRING,
    url STRING)
STORED AS PARQUET;
```

会话分析SQL

```sql
-- 找到会话的边界
-- 通过比较当前记录和前一条记录的timestamp字段, 如果间隔时间超过5分钟（假设），则赋值为1，否则为0
with session_start as (
  SELECT
    CASE 
      WHEN timestamp - LAG(timestamp, 1, 0) OVER (PARTITION BY ip, user_agent ORDER BY timestamp) > 300 -- 5 minutes
        THEN 1 
      ELSE 0 
      END as session_boundary,
    request_time,
    timestamp,
    ip,
    user_agent,
    url
  FROM mw_tokenized_access_logs_${date}
),
-- 填充会话编号
-- 通过对会话边界的累加，每个会话会有一个自增长的会话编号
sn as (
  SELECT
    SUM(session_boundary) OVER (PARTITION BY ip, user_agent ORDER BY timestamp) as session_number,
    *
  FROM session_start
),

-- 为每一个会话生成一个唯一的会话ID
t_session_id as (
  SELECT
    reflect('org.apache.commons.codec.digest.DigestUtils', 'shaHex',
      CONCAT(
        FIRST_VALUE(timestamp) 
          OVER (PARTITION BY ip, user_agent, session_number ORDER BY timestamp), 
        ip, user_agent)
    )
    as session_id,
    *
  FROM sn
)

insert overwrite table mw_sessions
select * from t_session_id;

select session_id, ip, user_agent, request_time, url from mw_sessions limit 10;
```