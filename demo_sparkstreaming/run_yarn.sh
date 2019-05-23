#!/usr/bin/env bash


spark-submit               \
 --master yarn             \
 --deploy-mode cluster     \
 --driver-memory   512m      \
 --executor-memory 512m     \
 --total-executor-cores 3  \
 --class com.gupao.bd.spark_streaming.demo.NetworkWordCount \
./demo_sparkstreaming-1.0-SNAPSHOT.jar localhost 9999 hdfs://gp-bd-master01:8020/tmp/wuchen/wordcount