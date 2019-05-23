# 创建数据库

drop DATABASE  if EXISTS wuchen;

CREATE DATABASE IF NOT EXISTS wuchen DEFAULT CHARSET utf8 COLLATE utf8_general_ci;


# 创建用户

drop USER  wuchen@'%';

create user wuchen@'%' IDENTIFIED  by '123456';

# grant all on wuchen.* to wuchen@'%' IDENTIFIED  by '123456';

#必须
grant all on wuchen.* to wuchen@'gp-bd-master01' IDENTIFIED  by '123456';

grant all on wuchen.* to wuchen@'localhost' IDENTIFIED  by '123456';

# revoke all on wuchen.* from  wuchen@'gp-bd-master01' IDENTIFIED  by '123456';


# 创建user表
drop TABLE  if EXISTS wuchen.user;

create table if not exists wuchen.user(
   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
   user_name VARCHAR(128) NOT NULL COMMENT '用户名',
   mobile_num VARCHAR(11) COMMENT '手机号码',
   register_time DATETIME COMMENT '注册时间',
   city VARCHAR(32) COMMENT '所属城市',
   create_time DATETIME COMMENT '创建时间',
   update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户表';


insert into wuchen.user(id,user_name,mobile_num,register_time,city) VALUE (10,'test','110','2018','sh');


# 创建product表
drop TABLE  IF EXISTS  wuchen.product;

create table if not exists wuchen.product(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  p_name VARCHAR(128) NOT NULL COMMENT '产品名称',
  p_category VARCHAR(11) COMMENT '产品类别',
  create_time DATETIME COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '产品表';


# 创建trade表
drop TABLE  if  EXISTS  wuchen.trade;

create table IF NOT EXISTS wuchen.trade(
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  order_id BIGINT COMMENT '订单ID',
  p_id VARCHAR(11) COMMENT '产品ID',
  user_id BIGINT COMMENT '用户ID',
  order_status VARCHAR(11) COMMENT '订单状态',
  order_time DATETIME  COMMENT '下单时间',
  buy_count INT COMMENT '购买数量',
  total_price DECIMAL COMMENT  '总金额',
  create_time DATETIME COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '交易表';



