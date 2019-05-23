#mock_www:模拟电商交易#

1、模块介绍
--------------------------------
 - package.json : 本模块所依赖的node.js库列表
 - node_modules : 本模块所依赖的node.js库（通过 npm install安装）
 - conf : 配置文件目录(mysql连接信息)
 - conf.js : 解析配置文件
 - mock_www.js : 模拟电商交易过程
 - startup.sh : 启动脚本
 - stop.sh : 停止脚本
 
            
2、如何运行该模块
---------------------------------
 - 确保已经安装nodejs和npm(最新版本的nodejs安装包中已经包含npm,下载地址：https://nodejs.org/en/download/）
 - 如node_modules目录不存在，可以通过npm install安装
 - 修改conf中的配置信息，指向MySQL库
 - 运行模拟程序：./startup.sh
 - 观察console中的输出  
       
   
      
   
      

  