
```sql
CREATE EXTERNAL TABLE access_log(
    date_time STRING,
    code STRING,
    path STRING,
    userid STRING,
    softid STRING
    )
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
    'input.regex' = '\\[([^\\]]*)\\] - (\\d*) - \\[\\d*.\\d*\\(s\\)\\] - "GET ([^?]*)[^&]*&[^=]*=([\\d]*)&[^=]*=([\\d]*)[^ ]* [^ ]* - [^-]* - [^ ]*',
    'output.format.string' = "%1$$s %2$$s %3$$s %4$$s %5$$s")
LOCATION '/data/test1/';