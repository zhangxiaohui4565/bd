# 运行步骤
1. mvn install
2. 将 jar 文件上传到hadoop集群
3. 运行：storm jar ./bd-storm.jar com.gp.bd.sample.storm.WordCountTopology
4. 停止：storm kill word-count
5. 如何查看日志：在supervisor节点的STORM_HOME/logs/workers-artifacts/TOPOLOGY_NAME/ 目录