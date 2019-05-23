package com.gp.bd.sample.storm.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.Random;

public class SplitSentenceBolt extends BaseRichBolt {

    OutputCollector _collector;
    Random random = new Random();
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

        _collector = collector;
    }

    public void execute(Tuple input) {
        String sentence = input.getString(0);
        for(String word: sentence.split("\\s+")) {
            _collector.emit(input, new Values(word,1));  // 建立 anchor 树
        }
//        if(random.nextInt(10) % 3==0){
//             _collector.fail(input);
//         }{
//            _collector.ack(input);
//        }

        _collector.ack(input);

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word","cnt"));
    }
}
