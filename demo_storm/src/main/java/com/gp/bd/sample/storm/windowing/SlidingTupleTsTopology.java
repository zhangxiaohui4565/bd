package com.gp.bd.sample.storm.windowing;

import com.gp.bd.sample.storm.bolt.PrintBolt;
import com.gp.bd.sample.storm.bolt.SlidingWindowSumBolt;
import com.gp.bd.sample.storm.spout.RandomIntegerSpout;
import com.gp.bd.sample.storm.utils.Utils;
import org.apache.storm.Config;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;

import java.util.concurrent.TimeUnit;


/**
 * 滑动窗口
 */
public class SlidingTupleTsTopology {

    private static String TOPOLOGY_NAME = "SlidingTupleTsTopology";

    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();

        BaseWindowedBolt windowedBolt = new SlidingWindowSumBolt()
                .withWindow(new BaseWindowedBolt.Duration(5, TimeUnit.SECONDS), new BaseWindowedBolt.Duration(2, TimeUnit.SECONDS))
                //通过withTimestampField指定tuple的某个字段作为这个tuple的timestamp
                .withTimestampField("ts")
                //输入流中最新的元组时间戳的最小值减去Lag值=watermark，用于指定触发watermark的的interval，默认为1秒
                //当watermark被触发的时候，tuple timestamp比watermark早的window将被计算
                .withWatermarkInterval(new BaseWindowedBolt.Duration(1, TimeUnit.SECONDS))
                //withLag用于处理乱序的数据，当接收到的tuple的timestamp小于等lastWaterMarkTs(`取这批watermark的最大值`)，则会被丢弃
                .withLag(new BaseWindowedBolt.Duration(5, TimeUnit.SECONDS));


        builder.setSpout("spout", new RandomIntegerSpout(), 1);
        builder.setBolt("sliding-sum", windowedBolt, 1).shuffleGrouping("spout");
        builder.setBolt("printer", new PrintBolt(), 1).shuffleGrouping("sliding-sum");

        Config conf = new Config();

        conf.setNumWorkers(1);

        if(args != null && args.length > 0){
            Utils.submitTopology("prod",TOPOLOGY_NAME,conf,builder.createTopology());
        }else{
            Utils.submitTopology("local",TOPOLOGY_NAME,conf,builder.createTopology());
        }
    }
}
