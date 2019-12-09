# Hive

## HIVE基础

### hive架构

#### 用户接口（CLI、JDBC）

#### Driver四大神器

##### 解析器

##### 编译器

##### 优化器

##### 执行器

#### 底层读取HDFS上数据，读时检查

### hive与数据库的区别

#### 延迟高

#### 数据量大

#### 不支持更新

#### 读时检查

### 优缺点

#### 优点

##### 接口采用类SQL语法，操作简单

##### 避免写MR程序

##### hive支持自定义函数

#### 缺点

##### 不支持记录级别的增删改操作

##### 延迟高

##### 不支持事务

### 交互方式

#### cli

#### jdbc\odbc

#### hive命令

##### hive -e sql语句

##### hive -f sql文件

### 数据类型

#### 基本数据类型

#### 复合数据类型

##### array Array(1,2)

##### map Map('a',1)

##### struct

###### 类对象数据格式

###### Struct（'a',1,2）

##### create table complex(
	     col1 array<int>,
	     col2 map<string,int>,
	     col3 struct<a:string,b:int,c:double>
)

### 类型转换

#### 隐式数据类型转换

#### 强制（手动转换）

##### cast(字段 as 数据类型)

### DDL

#### Data Definition Language

##### 内部表

###### 删除表时元数据也跟着删除

###### 数据存储路径在指定路径下

##### 外部表

###### 删除表时元数据还在hdfs上进行存储

## DDL和DML

### DDL 操作

#### 修改表名称

#### 查看表结构

#### 操作列信息

#### 操作查看分区信息

##### 添加分区需要在现有分区之上进行添加

##### 删除分区的时候会将所有存在的分区删除掉

#### 建表时使用like 复制表结构

#### create table 表名1 as select * from 表2 

### HIve分区表

#### 静态分区

##### 定义表PARTITIONED BY(month string)

##### partition(month='201909')

##### hdfs dfs -put nameinfo.txt /user/hive/warehouse/tb_partition/month=201910

#### 动态分区

##### 数据不能直接进行load

##### 借助一张全数据列的表存储全部字段

##### 从全数据表中进行导入数据，将其中一个列作为分区依据

##### 需要设置为nostrict

###### set hive.exec.dynamic.partition=true

###### set hive.exec.dynamic.partition.mode=nostrict

##### egg

###### insert into table partition_table partition(order_time) select ordernumber, order_time, order_price from base_table

### 加载数据

#### load data [local] path overwrite/into table 表 [partition (parcol=val1)]

### 分桶

#### 基础设置

##### set hive.enforce.bucketing=true

##### setmapreduce.job.reduces=4(分桶个数)

#### 创建表

##### clustered by(col)
into 4 backets ...

#### 查询

##### select * from backets_table tablesample (backet 1 out of 2)

##### 总共需要桶4/2

##### 从第一个桶取数据

#### 与分区的区别

##### 分区可以理解为文件夹，避免全表扫描，注意分区个数别太多避免小文件的产生

##### 分桶主要是为了抽样，将文件在进行区分，可以理解为分区的细粒度

#### 注意：桶表数据也不能直接进行数据导入

## 查询

### 基本查询

#### 大小写不敏感

#### 关键字

### 排序查询

#### orderby 全表排序最终形成一个reduce程序

#### sortBy

##### sortby 和distribute by 结合使用

##### 当前者两个字段一致时等价成cluster by（col）

### 小数据量避免启动MR

#### 本地模式

##### set hive.exec.mode.local.auto=true

##### 文件数少于5会进行本地计算，避免进行资源调度

##### 文件大小小于一定128M（默认）

### join

#### 只支持等值join

#### 内连接 inner join

#### (left,right,full)outer join 

## 存储压缩

### 压缩

#### 设置

##### 中间结果压缩

###### set hive.exec.compress.intermediat=true

###### set mapred.map.output.compression.codec=
org.apach.hadoop.io.compress.Snappycodec;

##### 最终输出结果压缩

###### set hive.exec.compress.output=true

###### set mapred.output.compression.codec=
org.apach.hadoop.io.compress.Snappycodec;

#### 压缩算法

##### gzip

##### bzip2

##### lzo

##### snappy

###### 压缩比相对高但解压缩速度快，且可分割

##### NONE 不压缩

### 存储

#### 类型

##### orc

###### 默认采取zlib压缩

###### 一般选择Snappy

###### 基于列进行存储的

##### parquet

##### textfile

#### 压缩比

##### orc> parquet>textfile

#### 查询速率

##### orc>textfile>textFile

####  STORED AS TEXTFILE

### 查看数据量的大小

#### dfs -du -h /user/hive/warehouse/...

## 升级操作

### 自定义函数

#### 临时函数

##### 继承org.apache.hadoop.hive.ql.exec.UDF
实现自己的函数打成jar包

##### 上传jar包到本地文件路径下

##### add jar /usr/local/hive_data/hiveext.jar;

##### create temporary function stringext as 'com.lyz.bigdata.StringExt';
StringExt 为自己定义函数的类的名字

##### 一旦退出客户端临时函数就不再shengxiao 

#### 永久函数

##### 将自定义的jar包上传到HDFS上

##### create  function stringext as 'com.lyz.bigdata.StringExt' 
USING JAR 'hdfs://hadoop002:9000/lib/hive-1.0-SNAPSHOT.jar';

##### 全局有效

#### 种类

##### UDF

##### UDAF

##### UDTF

### SerDe（序列化、反序列化）

#### 内置类型

##### ORC

##### JsonSerDe

##### RegEx(正则)

###### create table doubledelimiter(id string,name string)
row format serde 'org.apache.hadoop.hive.serde2.RegexSerDe'
with serdeproperties('input.regex'='(.*)\\|\\|(.*)','output.format.string'='%1$s %2$s')
stored as textfile;

##### CSV

##### Parquet

##### LazySimpleSerDe （默认）

####  使用row format 参数说明SerDe的类型。

#### 解析多字节分隔符

## 调优

### join 优化

#### 小表join大表

##### 使用mapjoin

##### 通过网络传输分发到大表数据所在bloc块的dN上，进程共享（JVM）

##### 类似spark的广播变量，spark每个执行器一份数据

##### 最终可以实现在map阶段完成join操作 （shuffle）

##### select /*+ MAPJOIN(a) */ 
	　　　　　　a.c1, b.c1 ,b.c2
　　　　　from a join b 
　　　　　where a.c1 = b.c1; 

#### 大表join大表

##### 检查能否使用combine操作

##### 避免笛卡尔积

#### join 字段有为空的

##### id为空的不进行参与关联
on 条件直接进行过滤

##### 给空值进行随机分配key

###### select * from log a 
 left outer join users b 
 on 
 case when a.user_id is null 
 then concat(‘hive’,rand() ) 
 else a.user_id end = b.user_id; 

### 避免数据倾斜

#### MR数据倾斜

##### 根据数值的key进行分布式自定义分区

##### 使用combine操作

##### 使用抽样和范围分区操作

###### 根据抽样结果来预设分区边界值

###### TotalOrderPartitioner

##### 单个数据过大

###### 设置mapreduce.input.linerecordreader.line.maxlength来限制RecordReader读取的最大长度。RecordRe
ader在TextInputFormat和KeyValueTextInputFormat类中使用。默认长度没有上限。

### 本地设置优化

#### 数据量不大时可以使用本地进行计算，避免集群申请资源的时间

#### Fetch抓取 more->全局查找 limit 不执行MR

#### 推测执行

##### 开启可以让执行慢的task在其他节点重新启动一个，避
免因为网略问题导致执行慢，当一个执行完成后另一个
进程结束执行，注意数据重复插入问题。

#### 调整并行任务执行度默认8

#### JVM重用避免重复进程启动增加开销
同一个job可以进行重新利用

### MR优化

#### 小文件合并

#### orderby 必须加limit

#### 使用压缩存储减少内存空间占用。

### GroupBy

#### 使用sum（1）+ groupBy(col) 代替count（distinct）来达到使用多个task执行

#### 避免数据倾斜

##### set hive.map.aggr=true

##### set hive.groupby.skewindata=true

##### 原理生成两个MR，第一MR进行随机计算，达到负载均衡的目的，第二次的MR依据随机分配的聚合数据再次进行聚合操作

## 分支主题
