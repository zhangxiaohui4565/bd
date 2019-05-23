package com.gp.bd.sample.storm.kafka;


import com.gp.bd.sample.storm.utils.PropertiesUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.storm.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class SentenceProducer {
    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", PropertiesUtils.getPropertyValue("kafka.bootstrap.servers"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Random random = new Random();
        String[]         sentences = new String[]{
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
        //生产者发送消息
        String topic = PropertiesUtils.getPropertyValue("kafka.topic");
        Producer<String, String> producer = new KafkaProducer<String,String>(props);
        for (int i = 1; i <= 10000; i++) {
            String value = sentences[random.nextInt(10) %2];
            System.out.println("value:"+value);
            ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topic, value);
            producer.send(msg);
            Utils.sleep(100);
        }

        //列出topic的相关信息
        List<PartitionInfo> partitions = new ArrayList<PartitionInfo>() ;
        partitions = producer.partitionsFor(topic);
        for(PartitionInfo p:partitions)
        {
            System.out.println(">>>>partition info:"+p);
        }

        System.out.println("send message over...");
        producer.close();
    }
}
