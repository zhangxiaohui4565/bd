#!/usr/bin/env bash

spark-submit  \
 --master local[3]          \
 --deploy-mode client      \
 --driver-memory   1g      \
 --executor-memory 1g      \
 --total-executor-cores 3  \
 --conf spark.streaming.backpressure.enabled=false
 --class com.gupao.bd.spark_streaming.demo.dstream.NetworkWordCount \
./target/demo_sparkstreaming-1.0-SNAPSHOT.jar localhost 9999