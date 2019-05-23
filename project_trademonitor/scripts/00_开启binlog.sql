## 开启binlog

# 查看my.cnf的位置
mysql --help |grep my.cnf

[mysqld]  #必须添加
log_bin=mysql-bin #开启binlog
binlog_format=ROW #选择row模式
server-id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复

## 重启服务并验证

sudo service mysqld  restart
show variables like 'binlog_format';
show variables like 'log_bin';

## 权限配置

# drop user canal@'gp-bd-master01';

CREATE USER canal IDENTIFIED BY 'canal';

# GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%' IDENTIFIED  by 'canal';

GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'localhost' IDENTIFIED  by 'canal';

GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'gp-bd-master01' IDENTIFIED  by 'canal';

FLUSH PRIVILEGES;

show grants for 'canal';