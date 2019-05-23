运行方式：

1. mvn clean package
2. 将生成的jar包上传到hadoop集群
3. 使用 hadoop jar XXX.jar com.gupao.bigdata.mapreduce.topn.TopNDriver /xxx/wordcount/output /xxx/topn/output 执行