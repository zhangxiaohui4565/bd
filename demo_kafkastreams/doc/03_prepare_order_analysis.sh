#!/usr/bin/env bash

#1、创建输入topic

bin/kafka-topics.sh --create \
--zookeeper dev-bg-02:2181/kafka \
--replication-factor 1 \
--partitions 1 \
--topic gp_items

bin/kafka-topics.sh --create \
--zookeeper dev-bg-02:2181/kafka \
--replication-factor 1 \
--partitions 1 \
--topic gp_users

bin/kafka-topics.sh --create \
--zookeeper dev-bg-02:2181/kafka \
--replication-factor 1 \
--partitions 1 \
--topic gp_orders

bin/kafka-topics.sh --create \
--zookeeper dev-bg-02:2181/kafka \
--replication-factor 1 \
--partitions 1 \
--topic gp_orderuser-repartition-by-item1

#2、输出topic

bin/kafka-topics.sh --create \
--zookeeper dev-bg-02:2181/kafka \
--replication-factor 1 \
--partitions 1 \
--topic gp_item-amount2 \
--config cleanup.policy=compact

bin/kafka-topics.sh --delete \
--zookeeper dev-bg-02:2181/kafka \
--topic gp_item-amount2


#3、验证topic是否创建成功

bin/kafka-topics.sh --zookeeper dev-bg-02:2181/kafka --describe --topic gp.*


#5、验证输出
bin/kafka-console-consumer.sh --bootstrap-server dev-bg-02:9092 \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--topic gp_item-amount