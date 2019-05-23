package com.gp.bd.sample.storm.bolt;

import org.apache.storm.state.KeyValueState;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseStatefulBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class WordCountWithStateBolt extends BaseStatefulBolt<KeyValueState<String, Long>> {

    private KeyValueState<String, Long> wordCounts;
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }
    @Override
    public void initState(KeyValueState<String, Long> state) {
        wordCounts = state;
    }
    @Override
    public void execute(Tuple tuple) {
        String word = tuple.getString(0);
        Long count = wordCounts.get(word, 0L);
        count++;

        wordCounts.put(word, count);
        collector.emit(tuple, new Values(word, count));
    }

}
