CREATE DATABASE opencart_stat CHARACTER SET utf8 COLLATE utf8_general_ci;

create table pv_stat (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `product_id` varchar(128) NOT NULL COMMENT '产品ID',
    `name` varchar(512) NOT NULL COMMENT '产品名字',
    `value` bigint(20) NOT NULL COMMENT '统计数值',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='PV统计表';

create table uv_stat (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `product_id` varchar(128) NOT NULL COMMENT '产品ID',
    `name` varchar(512) NOT NULL COMMENT '产品名字',
    `value` bigint(20) NOT NULL COMMENT '统计数值',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='UV统计表';

create table duration_stat (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `product_id` varchar(128) NOT NULL COMMENT '产品ID',
    `name` varchar(512) NOT NULL COMMENT '产品名字',
    `value` decimal(40,8) NOT NULL COMMENT '统计数值',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='页面平均停留时间';