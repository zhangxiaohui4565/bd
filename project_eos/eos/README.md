
# 工程介绍

本工程是日志搜索项目(eos)服务端：收集日志，搜索、管理日志等

# 目录结构

- collector
  日志收集服务端
  
- query
  日志搜索、管理api
  
- commons
  公共类包
  
- misc
  依赖组件启动命令，依赖组件配置，日志template定义

# 项目依赖

- kafka
- elasticsearch
- hdfs

# 运行步骤

- 启动依赖服务
  - kafka部署，启动，创建topic
  - es部署，启动，创建template
  - hadoop部署启动，创建备份目录，配置权限

  启动命令，创建topic，创建目录等详见：misc目录文档

- collector
  运行 com.gupao.bd.eos.collector.App，参数：-c conf/eos-collector.properties -l conf/log4j.properties
  
- query
  添加tomcat应用，选deployment：eos-query，即可启动tomcat
  