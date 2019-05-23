package com.gupao.bd.kafka.demo;

import com.gupao.bd.kafka.serde.Customer;
import com.gupao.bd.kafka.serde.CustomerDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * kafka consumer
 */
public class CustomerConsumer implements IConfig {

    public static void main(String[] args) {

        //第一步：配置参数
        Properties props = new Properties();

        //broker地址：建立与集群的连接，通过一个broker会自动发现集群中其他的broker,不用把集群中所有的broker地址列全，一般配置2-3个即可
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);

        //consumer_group id
        props.put("group.id", "gp-kafka-demo");

        //自动提交offset
//        props.put("enable.auto.commit", "true");
//        props.put("auto.commit.interval.ms", "1000");

        //手动提交offset
        props.put("enable.auto.commit", "false");

        //consumer每次发起fetch请求时,从服务器读取的数据量
        props.put("max.partition.fetch.bytes", "1000");

        //一次最多poll多少个record
        props.put("max.poll.records", "5");

        //跟producer端一致(bytes->object)
        props.put("key.deserializer", StringDeserializer.class.getCanonicalName());

        //跟producer端一致
        props.put("value.deserializer", CustomerDeserializer.class.getCanonicalName());

        //第二步：创建KafkaConsumer
        Consumer<String, Customer> consumer = new KafkaConsumer(props);

        //第三步：订阅消息,指定topic
        boolean isGroupMode = true;

        if (isGroupMode) {
            //群组模式：新增/移除消费者会触发再均衡
            consumer.subscribe(Arrays.asList(TOPIC_NAME));
        } else {
            //独立消费者模式：只消费指定的分区，新增/移除消费者不会触发再均衡
            List<TopicPartition> partitions = new ArrayList<>();
            List<PartitionInfo> partitionInfoList = consumer.partitionsFor(TOPIC_NAME);
            for (PartitionInfo p : partitionInfoList) {
                partitions.add(new TopicPartition(p.topic(), p.partition()));
            }
            consumer.assign(partitions);
        }

        while (true) {
            try {
                //第四步：消费消息, 100是timeout时间，以毫秒为单位
                ConsumerRecords<String, Customer> records = consumer.poll(100);

                System.out.println("获取数据量：" + records.count());

                for (ConsumerRecord<String, Customer> record : records) {
                    System.out.printf("开始消费数据:offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                }

                consumer.commitSync();

                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.close();
            }
        }
    }
}
