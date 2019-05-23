package com.gp.bd.sample.storm.app;


import com.gp.bd.sample.storm.bolt.PrintBolt;
import com.gp.bd.sample.storm.bolt.SplitSentenceBolt;
import com.gp.bd.sample.storm.bolt.WordCountBolt;
import com.gp.bd.sample.storm.utils.PropertiesUtils;
import com.gp.bd.sample.storm.utils.Utils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class WordCountTopology {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        if (args.length == 0) {
            System.out.println("Usage: plase input runEnv");
            System.exit(1);
        }

        String runEnv = args[0];

        //1、构建TopologyBuilder
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // 构造随机 spout
        // topologyBuilder.setSpout("spout",new RandomSentenceSpout(),2);//并发度2

        final String kafkaTopic = PropertiesUtils.getPropertyValue("kafka.topic");

        // 构造kafka spout
        topologyBuilder
                .setSpout("spout", new KafkaSpout(Utils.buildKafkaConfig(kafkaTopic)), 1);

        topologyBuilder
                .setBolt("split", new SplitSentenceBolt(), 2)
                .shuffleGrouping("spout");//并发度为2，随机方式发送到task

        topologyBuilder
                .setBolt("count", new WordCountBolt(), 4)
                .fieldsGrouping("split", new Fields("word"));//并发度为4，按key发送到task

        topologyBuilder.setBolt("print", new PrintBolt(), 1)
                .shuffleGrouping("count");

        //2、config
        Config config = new Config();
        config.setDebug(false);
        config.setNumAckers(1);
        config.setMaxSpoutPending(1000);
        config.setNumWorkers(2);
        config.setTopologyWorkerMaxHeapSize(1024);
        config.put("show.topNum", PropertiesUtils.getPropertyValue("show.topNum"));


        //3、提交任务  -----两种模式 本地模式和集群模式
        if ("local".equals(runEnv)) {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("word_count_topology", config, topologyBuilder.createTopology());
        } else if ("prod".equals(runEnv)) {
            StormSubmitter.submitTopology("word_count_topology", config, topologyBuilder.createTopology());
        } else {
            System.err.println(">>>非法环境变量值");
        }

    }


}