[mysqld]  #必须添加
log_bin=mysql-bin #开启binlog
binlog_format=ROW #选择row模式
server-id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复

## 重启服务并验证

sudo service mysqld  restart
show variables like 'binlog_format';
show variables like 'log_bin';