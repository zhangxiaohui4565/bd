#!/usr/bin/env bash

# create topic

    kafka-topics --zookeeper gp-bd-master01:2181/kafka --delete  --if-exists --topic demo_kafka

    kafka-topics --zookeeper gp-bd-master01:2181/kafka --create --if-not-exists --topic demo_kafka --partitions 2 --replication-factor 2

    kafka-topics --zookeeper gp-bd-master01:2181/kafka --describe --topic demo_kafka

    kafka-topics --zookeeper gp-bd-master01:2181/kafka --list

# producer

   kafka-console-producer --broker-list gp-bd-master01:9092 --topic demo_kafka

# consumer

   kafka-console-consumer --bootstrap-server gp-bd-master01:9092 --topic demo_kafka --from-beginning
