package com.gp.bd.sample.storm.bolt;

import java.util.List;
import java.util.Map;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;



public class SlidingWindowSumBolt extends BaseWindowedBolt {

    private int sum = 0;
    private OutputCollector collector;


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow inputWindow) {


        List<Tuple> tuplesInWindow = inputWindow.get();
        List<Tuple> newTuples = inputWindow.getNew();
        List<Tuple> expiredTuples = inputWindow.getExpired();

        System.out.println("------------total---------");
        for(Tuple tuple:tuplesInWindow){
            System.out.print(tuple.getValue(0)+",");
        }
        System.out.println();
        System.out.println("--------------------------");

        System.out.println("------------new---------");
        for(Tuple tuple:newTuples){
            System.out.print(tuple.getValue(0)+",");
        }

        System.out.println();
        System.out.println("--------------------------");

        System.out.println("--------expired-----------");

        for(Tuple tuple:expiredTuples){
            System.out.print(tuple.getValue(0)+",");
        }
        System.out.println();
        System.out.println("-------------------------");

        for (Tuple tuple : newTuples) {
            sum += (int) tuple.getValue(0);
        }
        for (Tuple tuple : expiredTuples) {
            sum -= (int) tuple.getValue(0);
            System.err.println("expired value:"+ tuple.getValue(0) +" ts:"+tuple.getValue(1));
        }
        collector.emit(new Values(sum));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sum"));
    }
}