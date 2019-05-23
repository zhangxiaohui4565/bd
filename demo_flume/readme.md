#本项目包含如下内容：
1. 日志生成代码
2. flume写入hdfs配置
3. flume写入hdfs和kafka配置

#日志生成代码运行步骤：

1. 运行 mvn clean install 打包
2. 将打好的bd-flume.jar包，lib/log4j-1.2.17.jar 和 config/log4j.properties 文件上传到运行目录
3. 在运行目录执行： java -classpath ./bd-flume.jar:./log4j-1.2.17.jar:./ com.gupao.bd.sample.flume.LogGenerator