package com.gp.bd.sample.storm.orders;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class AreaOrderParserBolt extends BaseBasicBolt {

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("area_id","order_amt","order_date"));
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String order = input.getString(0);
        if(order != null && order.length()>0){
            String[] arr = order.split("\\t");
            collector.emit(new Values(arr[3],arr[1],arr[2].substring(0,10)));
            //1001 100 2019-01-27
        }
    }
}
