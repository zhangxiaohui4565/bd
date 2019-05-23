#!/usr/bin/env bash

# topic gp_user
bin/kafka-topics.sh --delete --zookeeper localhost/kafka  --if-exists  --topic gp_user
bin/kafka-topics.sh --create --zookeeper localhost/kafka  --if-not-exists --replication-factor 1 --partitions 1 --topic gp_user

# topic gp_trade
bin/kafka-topics.sh --delete --zookeeper localhost/kafka  --if-exists  --topic gp_trade
bin/kafka-topics.sh --create --zookeeper localhost/kafka  --if-not-exists --replication-factor 1 --partitions 1 --topic gp_trade

# topic gp_metric
bin/kafka-topics.sh --delete --zookeeper localhost/kafka  --if-exists  --topic gp_metric
bin/kafka-topics.sh --create --zookeeper localhost/kafka  --if-not-exists --replication-factor 1 --partitions 1 --topic gp_metric


# 验证topic是否创建成功

bin/kafka-topics.sh --describe --zookeeper localhost/kafka   --topic gp_user
bin/kafka-topics.sh --describe --zookeeper localhost/kafka   --topic gp_trade
bin/kafka-topics.sh --describe --zookeeper localhost/kafka   --topic gp_metric
