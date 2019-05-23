#!/usr/bin/env bash

nohup bin/storm nimbus >> /dev/null &
nohup bin/storm supervisor >> /dev/null &
nohup bin/storm ui >> /dev/null &

bin/storm jar /cloud/code/jd/storm-example/target/storm-example-1.0-SNAPSHOT.jar  com.gupao.bd.realtime.app.WordCountTopology prod


bin/storm jar /cloud/code/gp/gp-bd/demo_storm/target/bd-storm-jar-with-dependencies.jar  com.gp.bd.sample.storm.orders.AreaOrderTopology area_order_topology