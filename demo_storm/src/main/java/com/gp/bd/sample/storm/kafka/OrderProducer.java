package com.gp.bd.sample.storm.kafka;


import com.gp.bd.sample.storm.utils.PropertiesUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.storm.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class OrderProducer {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String area_id[] = {"1001", "1002", "1003", "1004", "1005"};
    private final static String order_amt[] = {"10.10", "20.3", "30.6", "40.5", "55.8"};
    private final static Random random = new Random();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", PropertiesUtils.getPropertyValue("kafka.bootstrap.servers"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        String orderTopicName = "area_order";

        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        int i = 0;
        while (true) {
            i++;
            try {
                String msg = i + "\t" + order_amt[random.nextInt(5)] + "\t" + formatter.format(new Date()) + "\t" + area_id[random.nextInt(5)];

                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(orderTopicName, "1", msg);
                producer.send(producerRecord);
                System.out.println(">>>>"+msg);
                Thread.sleep(5000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
