1、工程介绍（project_trademonitor:实时交易监控系统）
----------------------------------------------------------
 - order_collection：订单采集,Java代码
 - metric_compute：指标计算(4组指标),Scala代码
 - monitor_service：监控服务(4组图表),NodeJS代码(js/html/css)
 - mock_www：模拟电商交易,NodeJS代码
 
            
2、如何运行该系统
----------------------------------------------------------
 - 确保已经安装Maven/MySQL/Canal Server/Kafka/Spark/NodeJs
 - 修改各模块下conf目录中的配置：将相关的ip,port等指向自己的集群(如CDH)
 - 参考scripts目录下的脚本，在mysql中建表／在kafka中创建topic
 - 运行package.sh对系统打包(仅针对订单采集／指标计算两个模块)
 - 启动系统：./startup.sh，停止系统：./stop.sh,或者通过以下命令分别启动各模块
   
      1) ./order_collection/startup.sh：运行订单采集Job
     
      2) ./metric_compute/startup.sh: 运行指标计算Job
       
      3) ./mock_www/startup.sh：运行模拟电商交易（随机往MySQL表中insert数据）
       
      4) ./monitor_service/startup.sh：运行监控后台服务
       
 - 观察各模块下的log日志信息
 - 在浏览器中输入网址：http://localhost:3030/monitor.html      
   
      
   
      

  