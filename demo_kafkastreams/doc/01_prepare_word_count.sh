#!/usr/bin/env bash

#1、创建输入topic

bin/kafka-topics.sh --create \
--zookeeper dev-bg-02:2181/kafka \
--replication-factor 1 \
--partitions 1 \
--topic streams-plaintext-input

#2、输出topic

bin/kafka-topics.sh --create \
--zookeeper dev-bg-02:2181/kafka \
--replication-factor 1 \
--partitions 1 \
--topic streams-wordcount-output \
--config cleanup.policy=compact


#3、验证topic是否创建成功

bin/kafka-topics.sh --zookeeper dev-bg-02:2181/kafka --describe --topic streams.*

#4、输入数据

bin/kafka-console-producer.sh --broker-list dev-bg-02:9092 --topic streams-plaintext-input


#5、验证输出

bin/kafka-console-consumer.sh --bootstrap-server dev-bg-02:9092 \
--topic streams-wordcount-output \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer


bin/kafka-console-consumer.sh --bootstrap-server dev-bg-02:9092 \
--topic streams-wordcount-output \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true