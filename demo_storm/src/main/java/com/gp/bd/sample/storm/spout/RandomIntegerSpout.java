package com.gp.bd.sample.storm.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

public class RandomIntegerSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(RandomIntegerSpout.class);
    private SpoutOutputCollector collector;
    private Random rand;
    private long msgId = 0;

    private int cnt = 0;
    private int startTs = 1;


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("value", "ts", "msgid"));
    }


    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.rand = new Random();
    }

    @Override
    public void nextTuple() {
        long ts = System.currentTimeMillis();
        cnt++;
        int value = 2;
        if (cnt % 9 == 0) {
            ts = System.currentTimeMillis() - 6 * 1000;
            value = 5;
            cnt = 1;
        }
        collector.emit(new Values(cnt, ts, ++msgId), msgId);
        Utils.sleep(1000);

    }

    @Override
    public void ack(Object msgId) {
        LOG.debug("Got ACK for msgId : " + msgId);
    }

    @Override
    public void fail(Object msgId) {
        LOG.debug("Got FAIL for msgId : " + msgId);
    }
}