package com.gp.bd.sample.storm.bolt;

import com.gp.bd.sample.storm.utils.MapSortUtil;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWindowBolt extends BaseWindowedBolt {

    private Map<String, Long> counters = new HashMap<>();
    private long topNum = 5;

    @Override
    public void execute(TupleWindow inputWindow) {

        List<Tuple> words = inputWindow.get();

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i).getString(0);
            Long count = counters.containsKey(word)?counters.get(word)+1:1L;
            counters.put(word, count);
        }

        counters = MapSortUtil.sortByValue(counters);
        long length = topNum < counters.keySet().size()?topNum:counters.keySet().size();
        int count = 0;

        for (String key : counters.keySet()) {
            if (count >= length) {
                break;
            }
            System.out.println("[" + key + ":" + counters.get(key) + "]");
            count++;
        }


    }
}
