1、模块介绍(order_collection:订单数据采集)
---------------------------------------
 - conf : 配置文件目录(canal及kafka等连接信息)
 - src : 源代码(Java程序)
 - pom.xml : 模块构建相关配置信息
 - package.sh : 打包脚本
 - run.sh: 运行脚本
 
           
2、如何运行该模块
-------------------------------------
 - 确保已经安装Maven/Canal Server/Kafka
 - 修改conf/job.properties中的配置信息
 - 程序打包：./package.sh
 - 运行采集程序：./run.sh
 - 观察console中的输出 

       
   
      
   
      

  