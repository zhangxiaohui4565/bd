package com.gp.bd.sample.storm.bolt;

import clojure.lang.Obj;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class PrintBolt extends BaseBasicBolt {
    public void execute(Tuple input, BasicOutputCollector collector) {
        try {
            Object msg = input.getValue(0);
            if (msg != null)
                System.out.println(msg);
                //或者落地到数据库或者消息中间价
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
