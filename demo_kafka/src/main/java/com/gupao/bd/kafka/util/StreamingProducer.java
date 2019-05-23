package com.gupao.bd.kafka.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * ./kafka-topics.sh --zookeeper localhost:2181/kafka --create --topic test-streaming --partitions 1 --replication-factor 1
 */
public class StreamingProducer {

    public static void main(String[] args) {

        //第一步：配置参数，影响producer的生产行为
        Properties props = new Properties();

        //broker地址：建立与集群的连接，通过一个broker会自动发现集群中其他的broker,不用把集群中所有的broker地址列全，一般配置2-3个即可
        props.put("bootstrap.servers", "localhost:9092");

        //是否确认broker完全接收消息：[0, 1, all]
        props.put("acks", "all");

        //失败后消息重发的次数：可能导致消息的次序发生变化
        props.put("retries", 2);

        //单批发送的数据条数
        props.put("batch.size", 16384);

        //数据在producer端停留的时间：设置为0，则立即发送
        props.put("linger.ms", 1);

        //数据缓存大小
        props.put("buffer.memory", 33554432);

        //key序列化方式
        props.put("key.serializer", StringSerializer.class.getCanonicalName());

        //value序列化方式
        props.put("value.serializer", StringSerializer.class.getCanonicalName());


        //第二步：创建KafkaProducer
        KafkaProducer<String, String> producer = new KafkaProducer(props);

        for (int i = 0; i < 100000000; i++) {
            try {
                String customer = "Tom" + (i % 10);

                System.out.println("send:" + customer);

                //第三步：构建一条消息
                ProducerRecord<String, String> record = new ProducerRecord<>("test_streaming", Integer.toString(i), customer);

                //第四步：向broker发送消息
                producer.send(record).get();


            } catch (Exception e) {
                e.printStackTrace();
                //关闭KafkaProducer，将尚未发送完成的消息全部发送出去
                producer.close();
            }
        }
    }
}
