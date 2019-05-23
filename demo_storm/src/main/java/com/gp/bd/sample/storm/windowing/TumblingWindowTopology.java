package com.gp.bd.sample.storm.windowing;

import com.gp.bd.sample.storm.bolt.PrintBolt;
import com.gp.bd.sample.storm.bolt.TumblingWindowAvgBolt;
import com.gp.bd.sample.storm.spout.RandomIntegerSpout;
import com.gp.bd.sample.storm.utils.Utils;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;


/**
 * 翻转窗口
 */
public class TumblingWindowTopology {

    private static String TOPOLOGY_NAME = "SlidingTupleTsTopology";

    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new RandomIntegerSpout(), 1);

        builder.setBolt("tumbling-avg", new TumblingWindowAvgBolt()
                .withTumblingWindow(BaseWindowedBolt.Count.of(10)), 1)
                .shuffleGrouping("spout");

        builder.setBolt("printer", new PrintBolt(), 1).shuffleGrouping("tumbling-avg");

        Config conf = new Config();
        conf.setNumWorkers(1);


        Config config = new Config();

        if(args != null && args.length > 0){
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        }else{
            Utils.submitTopology("local",TOPOLOGY_NAME,conf,builder.createTopology());
        }
    }
}
