package com.gp.bd.sample.storm.bolt;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class SplitSentenceBolt2 extends BaseBasicBolt {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
    }

    public void execute(Tuple input, BasicOutputCollector collector) {
        String line = input.getString(0);
        String[] arrWords = line.split("\\s+");
        for (String word : arrWords) {
            collector.emit(new Values(word, 1));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //声明输出的filed
        declarer.declare(new Fields("word", "cnt"));

    }

    //负责连接信息的关闭等
    @Override
    public void cleanup() {
        super.cleanup();
    }
}
