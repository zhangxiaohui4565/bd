package com.gp.bd.sample.storm.bolt;


import com.gp.bd.sample.storm.utils.MapSortUtil;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WordCountBolt extends BaseBasicBolt {
    private  Map<String, Long> counters = new ConcurrentHashMap<String, Long>();
    private long topNum = 10;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        if(stormConf.containsKey("show.topNum")){
            topNum = Long.valueOf(stormConf.get("show.topNum").toString());
        }
        super.prepare(stormConf, context);
    }

    public void execute(Tuple input, BasicOutputCollector collector) {
        String word = input.getStringByField("word");
        Integer cnt = input.getIntegerByField("cnt");
        if (counters.containsKey(word)){
            Long count = counters.get(word);
            counters.put(word,count + cnt);
        }else {
            counters.put(word,Long.valueOf(topNum));
        }

        long length = 0;
        //使用工具类MapSort对map进行排序
        counters = MapSortUtil.sortByValue(counters);
        if (topNum < counters.keySet().size()) {
            length = topNum;
        } else {
            length = counters.keySet().size();
        }
        int count = 0;
        for (String key : counters.keySet()) {
            if (count >= length) {
                break;
            }
            if (count == 0) {
                word = "[" + key + ":" + counters.get(key) + "]";
            } else {
                word = word + ", [" + key + ":" + counters.get(key) + "]";
            }
            count++;
        }

        String result  = "The first " + topNum + ": " + word;
        collector.emit(new Values(result));
    }


    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("output_word_stat"));
        //不輸出
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}