package com.gp.bd.sample.storm.orders;

import com.gp.bd.sample.storm.dao.HbaseDao;
import com.gp.bd.sample.storm.dao.HbaseDaoImpl;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;

import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

public class AreaOrderSinkBolt extends BaseBasicBolt {
    private Map<String, Double> countsMap = null;
    private  HbaseDao hbaseDao = null;
    private long startTime = System.currentTimeMillis();
    private long endTime = 0L;
    private long DATA_SINK_INTERVAL = 5 * 1000L;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        countsMap = new HashMap<>();
        hbaseDao = new HbaseDaoImpl();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String dateArea = input.getString(0);

        countsMap.put(dateArea, input.getDoubleByField("amt"));

        //每隔5秒入库
        endTime = System.currentTimeMillis();
        if (endTime - startTime >= DATA_SINK_INTERVAL) {
            for (String key : countsMap.keySet()) {
                hbaseDao.insert("demo_storm:storm_area_order", key, "info", "order_amt", countsMap.get(key) + "");
            }
            countsMap.clear();
            startTime = System.currentTimeMillis();
        }
    }
}