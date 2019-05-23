package com.gupao.bd.kafka_streams.demo.word_count;

import com.gupao.bd.kafka_streams.demo.BaseExample;
import com.gupao.bd.kafka_streams.processor.WordCountProcessor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Properties;

/**
 * Low Level API: 通过Processor API创建WordCount程序
 */
public class WordCountExample2 extends BaseExample {

    public static void main(final String[] args) throws Exception {

        //1、参数设置
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "WordCountExample2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //2、构建topology:通过自定义Processor
        Topology builder = new Topology();

        //定义state store
        StoreBuilder<KeyValueStore<String, Long>> countStoreSupplier =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore("Counts"),
                        Serdes.String(),
                        Serdes.Long()).withLoggingDisabled();

        builder.addSource("Source", KAFKA_INPUT_TOPIC)
                .addProcessor("Process", () -> new WordCountProcessor(), "Source")
                .addStateStore(countStoreSupplier, "Process")
                .addSink("Sink", KAFKA_OUTPUT_TOPIC, "Process");

        //3、创建KafkaStreams程序并启动
        KafkaStreams streams = new KafkaStreams(builder, props);
        streams.start();
    }
}
