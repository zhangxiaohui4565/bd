# 目录结构说明 #

- /rcmddata: 爬取电影封面，并将电影数据导入到ES
- /rcmdalg: 推荐算法实现
- /rcmdsys: 推荐应用系统

# 项目依赖组件 #

- hadoop 集群：保存评分数据，运行推荐算法
- mysql：同步算法结果
- elasticsearch：电影索引信息
- tomcat：运行推荐应用系统

# 运行步骤 #
1. 运行 rcmddata 下代码，获取电影封面，并将电影数据导入到ES。具体步骤参考 rcmddata\README 文件
3. 在 mysql 中运行 rcmdsys/db/create\_all.sql 脚本，生成数据库表结构
2. 打包 rcmdalg，将jar包上传到hadoop集群，运行算法。具体步骤参考 rcmdalg\README 文件
4. 修改 rcmdsys/rcmdsys.properties 文件，设置es等配置项，打包rcmdsys，部署到tomcat，运行
5. 浏览器中打开： localhost:8080/rcmdsys/index，访问系统主页 
