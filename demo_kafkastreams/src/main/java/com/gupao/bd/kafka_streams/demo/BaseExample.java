package com.gupao.bd.kafka_streams.demo;


public abstract class BaseExample {

    protected static String KAFKA_BOOTSTRAP_SERVERS = "dev-bg-01:9092";
    protected static String KAFKA_INPUT_TOPIC = "streams-plaintext-input";
    protected static String KAFKA_OUTPUT_TOPIC = "streams-wordcount-output";

}
