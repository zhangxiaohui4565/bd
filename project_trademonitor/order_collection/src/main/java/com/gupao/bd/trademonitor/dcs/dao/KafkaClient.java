package com.gupao.bd.trademonitor.dcs.dao;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 功能：封装kafka consumer/producer客户端,负责和kafka集群进行交互
 */
public class KafkaClient {

    private String bootstrapServers;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    public KafkaClient(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    private KafkaProducer<String, String> initProducer() {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", this.bootstrapServers);
        prop.put("key.serializer", StringSerializer.class.getName());
        prop.put("value.serializer", StringSerializer.class.getName());
        prop.put("acks", "1");
        prop.put("retries", "1");

        return new KafkaProducer<>(prop);
    }

    private KafkaConsumer<String, String> initConsumer(String consumerGrp) {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", this.bootstrapServers);
        prop.put("group.id", consumerGrp);
        prop.put("key.deserializer", StringDeserializer.class.getName());
        prop.put("value.deserializer", StringDeserializer.class.getName());
        prop.put("enable.auto.commit", "true");
        prop.put("auto.commit.interval.ms", "1000");
        prop.put("auto.offset.reset", "latest");

        return new KafkaConsumer<>(prop);
    }

    public void produce(String topic, String val) {
        produce(topic, null, val);
    }

    public void produce(String topic, String key, String val) {
        ProducerRecord record = new ProducerRecord(topic, key, val);

        if (this.producer == null) {
            this.producer = initProducer();
        }

        try {
            producer.send(record).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public ConsumerRecords<String, String> consume(List<String> topics, String consumerGroup) {
        if (this.consumer == null) {
            this.consumer = initConsumer(consumerGroup);
        }

        consumer.subscribe(topics);
        return consumer.poll(500);
    }
}

