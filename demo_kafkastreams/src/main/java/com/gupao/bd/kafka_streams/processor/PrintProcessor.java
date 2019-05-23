package com.gupao.bd.kafka_streams.processor;


import org.apache.kafka.streams.processor.AbstractProcessor;

public class PrintProcessor extends AbstractProcessor<String,Long> {

    @Override
    public void process(String key, Long value) {
        System.out.println("key = [" + key + "], value = [" + value + "]");
    }
}