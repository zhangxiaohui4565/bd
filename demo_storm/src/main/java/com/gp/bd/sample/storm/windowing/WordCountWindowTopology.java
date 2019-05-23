package com.gp.bd.sample.storm.windowing;


import com.gp.bd.sample.storm.bolt.SplitSentenceBolt;
import com.gp.bd.sample.storm.spout.RandomSentenceSpout;
import com.gp.bd.sample.storm.bolt.MyWindowBolt;
import com.gp.bd.sample.storm.utils.Utils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;



public class WordCountWindowTopology {


    private static String TOPOLOGY_NAME = "word_count_window_topology";

    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {

        //1、构建TopologyBuilder
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // 构造随机 spout
        topologyBuilder.setSpout("spout",new RandomSentenceSpout(),2);//并发度2


        topologyBuilder
                .setBolt("split",new SplitSentenceBolt(),2)
                .shuffleGrouping("spout");//并发度为2，随机方式发送到task


        topologyBuilder
                .setBolt("bolt", new MyWindowBolt()
                .withWindow(BaseWindowedBolt.Duration.seconds(50), BaseWindowedBolt.Duration.seconds(10)))
                .shuffleGrouping("split");


        //2、config
        Config config =  new Config();
        config.setDebug(false);
        config.setNumAckers(1);
        config.setMaxSpoutPending(2000);
        config.setMessageTimeoutSecs(120); //要保证超时时间大于等于窗口长度+滑动间隔长度

        config.setNumWorkers(1);
        config.setTopologyWorkerMaxHeapSize(1500);


        if(args != null && args.length > 0){
            StormSubmitter.submitTopology(args[0], config, topologyBuilder.createTopology());
        }else{
            Utils.submitTopology("local",TOPOLOGY_NAME,config,topologyBuilder.createTopology());
        }
    }


}