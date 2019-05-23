package com.gupao.bd.kafka_streams.demo.word_count;

import com.gupao.bd.kafka_streams.demo.BaseExample;
import com.gupao.bd.kafka_streams.processor.PrintProcessor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

/**
 * High Level API: 通过Stream DSL创建WordCount程序
 */
public class WordCountExample1 extends BaseExample {

    public static void main(final String[] args) throws Exception {

        //1、参数设置:StreamsConfig
        Properties streamsProp = new Properties();
        streamsProp.put(StreamsConfig.APPLICATION_ID_CONFIG, "WordCountExample1");
        streamsProp.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        streamsProp.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsProp.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //2、构建topology:通过DSL算子
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> textLines = streamsBuilder.stream(KAFKA_INPUT_TOPIC);

        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(Materialized.as("Counts"));

        wordCounts.toStream().to(KAFKA_OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
        wordCounts.toStream().process(() -> new PrintProcessor());

        //3、创建KafkaStreams程序并启动
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), streamsProp);
        streams.start();

//        streams.setUncaughtExceptionHandler((t, e) -> {
//            System.out.println("thread = [" + t.getName() + "]:" + e);
//        });
    }
}
