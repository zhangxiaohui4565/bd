package com.gupao.bd.kafka.demo;

import com.gupao.bd.kafka.partitioner.CustomerPartitioner;
import com.gupao.bd.kafka.serde.Customer;
import com.gupao.bd.kafka.serde.CustomerSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * kafka producer
 * <p>
 * 运行之前先创建topic:
 * <p>
 * ./kafka-topics.sh --zookeeper localhost:2181/kafka --create --replication-factor 1 --partitions 1 --topic  gp-kafka-demo-topic
 */
public class CustomerProducer implements IConfig {

    public static void main(String[] args) {

        //第一步：配置参数，影响producer的生产行为
        Properties props = new Properties();

        //broker地址：建立与集群的连接，通过一个broker会自动发现集群中其他的broker,不用把集群中所有的broker地址列全，一般配置2-3个即可
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);

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
        props.put("value.serializer", CustomerSerializer.class.getCanonicalName());

        //重新设置分区器
        props.put("partitioner.class", CustomerPartitioner.class.getCanonicalName());

        //第二步：创建KafkaProducer
        KafkaProducer<String, Customer> producer = new KafkaProducer(props);

        for (int i = 0; i < 100; i++) {
            try {
                Customer customer = new Customer(i, "name" + i);

                //第三步：构建一条消息
                ProducerRecord<String, Customer> record = new ProducerRecord<>(TOPIC_NAME, Integer.toString(i), customer);

                //第四步：向broker发送消息
                RecordMetadata rm = producer.send(record).get();

                System.out.println("send message:" + customer + ", partition=" + rm.partition() + ", offset=" + rm.offset());

                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
                //关闭KafkaProducer，将尚未发送完成的消息全部发送出去
                producer.close();
            }
        }
    }
}
