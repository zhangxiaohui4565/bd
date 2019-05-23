1、模块介绍(monitor_service:监控大屏服务)
--------------------------------------
 - package.json : 本模块所依赖的node.js库列表
 - node_modules : 本模块所依赖的node.js库（通过 npm install安装）
 - conf : 配置文件目录(kafka等连接信息)
 - conf.js : 解析配置文件
 - public : 监控大屏依赖的js/image/css文件及第三方图表库(echarts/highcharts)
 - monitor.html : 监控大屏-前台页面
 - monitor.js : 监控大屏-后台服务
 - startup.sh : 启动脚本
 - stop.sh : 停止脚本
 
            
2、如何运行该模块
-------------------------------------
 - 确保已经安装nodejs和npm(最新版本的nodejs安装包中已经包含npm,下载地址：https://nodejs.org/en/download/）
 - 如node_modules目录不存在，可以通过npm install安装
 - 修改conf中的配置信息，指向Kafka所在的ZK地址
 - 启动模拟程序：./startup.sh, 停止模拟程序：./stop.sh
 - 观察console中的输出 
 - 在浏览器中输入网址：http://localhost:3030/  
       
   
      
   
      

  