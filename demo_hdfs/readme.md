# 运行步骤
1. mvn install
2. 将 jar 文件上传到hadoop集群
3. 运行： hadoop jar XXX.jar com.gupao.bd.sample.hdfs.HDFSFileWrite %本地文件地址% %hdfs目标文件地址%
4. 运行： hadoop jar XXX.jar com.gupao.bd.sample.hdfs.HDFSFileRead %hdfs目标文件%