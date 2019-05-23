## 数据导入
1. 将 rcmddata/data/ratings.csv 上传到hdfs(注意去掉第一行):
```
    hdfs dfs -copyFromLocal LOCATION/ratings.csv /user/george/rcmd/sample/input/
```

2. 创建hive表：
```
    CREATE EXTERNAL TABLE ods.movie_rating(user_id int, movie_id int, rating float, rate_time int)
    ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
    STORED AS TEXTFILE
    location '/user/george/rcmd/sample/input/';
```

3. 打包  bd-rcmdalg.jar 并上传到hadoop集群

## TOPN 运行：
1. 执行hive语句查询topn的电影：
```
    INSERT overwrite directory '/user/george/rcmd/sample/output/topn/'  
    ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'  
    SELECT movie_id, count(user_id) as c
    FROM ods.movie_rating  
    GROUP BY movie_id
    ORDER BY c DESC
    LIMIT 20;
```

2. 使用sqoop将数据导入到mysql中：
```
sqoop export  \
 --connect "jdbc:mysql://gp-bd-master01:3306/rcmd_result?useUnicode=true&characterEncoding=utf-8"\
 --username root \
 --password pwd@9pD8 \
 --table alg_topn_movie \
 --m 1 \
 --export-dir /user/george/rcmd/sample/output/topn/ \
 --input-fields-terminated-by '\t'\
 --columns="movie_id,view_count" 
```

## CWBTIAB 运行：
1. 在hadoop上运行MapReduce任务：
```
    hadoop jar ./bd-rcmdalg.jar com.gupao.bd.sample.rcmd.algr.CwbtiabAlgr /user/george/rcmd/sample/input/ /user/george/rcmd/sample/tmp_output/ /user/george/rcmd/sample/ww_output/
```

2. 创建hive表(为检查结果，非必须):
```
    CREATE EXTERNAL TABLE related_movie(movie_id int, topn_related string)
    ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
    STORED AS TEXTFILE
    location '/user/george/rcmd/sample/ww_output';
```

3. 使用sqoop将数据导入到mysql中：
```
sqoop export  \
 --connect "jdbc:mysql://gp-bd-master01:3306/rcmd_result?useUnicode=true&characterEncoding=utf-8"\
 --username root \
 --password pwd@9pD8 \
 --table alg_related_movie \
 --m 1 \
 --export-dir /user/george/rcmd/sample/ww_output/ \
 --input-fields-terminated-by '\t'\
 --columns="src_movie_id,des_movies" 
```
    
## 基于物品的协同过滤运行：
1. 在集群上运行spark任务：
```
spark-submit --master yarn-client --class com.gupao.bd.sample.rcmd.algr.ItemBasedCFAlgr ./bd-rcmdalg.jar /user/george/rcmd/sample/input/ /user/george/rcmd/sample/ibcf_output 
```

2. 创建hive表(为检查结果，非必须):
```
    CREATE EXTERNAL TABLE ibcf_movie_similarity(src_movie_id int, dest_movie_id int, pearson float, cosine float, jaccard float)
    ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
    STORED AS TEXTFILE
    location '/user/george/rcmd/sample/ibcf_output';
```

3. 使用sqoop将数据导入到mysql中：
```
sqoop export  \
 --connect "jdbc:mysql://gp-bd-master01:3306/rcmd_result?useUnicode=true&characterEncoding=utf-8"\
 --username root \
 --password pwd@9pD8 \
 --table alg_ibcf_movie_similarity \
 --m 1 \
 --export-dir /user/george/rcmd/sample/ibcf_output/ \
 --input-fields-terminated-by ','\
 --columns="src_movie_id,des_movie_id,pearson,cosine,jaccard" 
```
