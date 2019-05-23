package com.gp.bd.sample.storm.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RandomSentenceSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private String [] sentences=null;
    private Random random;
    private Map<String, String> pending = new ConcurrentHashMap<String, String>();

    //初始化
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        random=new Random();//
        sentences = new String[]{
                "gp is a good company",
                "And if the golden sun",
                "four score and seven years ago",
                "storm hadoop spark hbase",
                "Would make my whole world bright",
                "storm would have to be with you",
                "Pipe to subprocess seems to be broken No output read",
                "You make me feel so happy",
                "For the moon never beams without bringing me dreams Of the beautiful Annalbel Lee",
                " But small change will not affect the user experience",
                "You need to add the above config in storm installation if you want to run the code",
                "In the latest version, the class packages have been changed from", "Now I may wither into the truth",
                "That the wind came out of the cloud",
                "at backtype storm utils ShellProcess",
                "Of those who were older than we"};
    }

    //上帝之手，循环调用[while(true)]，每调用过一次就发送一条消息
    public void nextTuple() {
        Utils.sleep(1000);
        String sentence= sentences[random.nextInt(sentences.length)];
        String messageId = UUID.randomUUID().toString().replaceAll("-", "");
        pending.put(messageId, sentence);
        collector.emit(new Values(sentence), messageId);
    }

    @Override
    public void ack(Object msgId) {
        System.out.println("消息处理成功" + msgId);
        System.out.println("删除缓存中的数据...");
        pending.remove(msgId);
    }


    @Override
    public void fail(Object msgId) {
        System.err.println("消息处理失败" + msgId);
        System.err.println("重新发送失败的信息...");
        collector.emit(new Values(pending.get(msgId)),msgId);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("spout"));
    }
}
