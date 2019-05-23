# 集成 kudu and impala

  https://www.cloudera.com/documentation/enterprise/5-14-x/topics/kudu_impala.html

# Using Apache Kudu with Apache Impala

   http://kudu.apache.org/docs/kudu_impala_integration.html

# create table

CREATE TABLE my_first_table
(
  id BIGINT,
  name STRING
)
PRIMARY KEY(id)
PARTITION BY HASH PARTITIONS 16
STORED AS KUDU
TBLPROPERTIES (
  'kudu_master_addresses' = 'gp-bd-master01:7051'
);


CREATE TABLE my_first_table (
id BIGINT,
name STRING
)
TBLPROPERTIES(
  'storage_handler' = 'com.cloudera.kudu.hive.KuduStorageHandler',
  'kudu.table_name' = 'my_first_table',
  'kudu.master_addresses' = 'gp-bd-master01',
  'kudu.key_columns' = 'id'
); 