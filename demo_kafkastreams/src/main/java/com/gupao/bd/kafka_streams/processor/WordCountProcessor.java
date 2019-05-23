package com.gupao.bd.kafka_streams.processor;


import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Optional;
import java.util.stream.Stream;

public class WordCountProcessor implements Processor<String, String> {

    private ProcessorContext context;
    private KeyValueStore<String, Long> kvStore;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        kvStore = (KeyValueStore) context.getStateStore("Counts");
    }

    @Override
    public void process(String key, String value) {
        System.out.println("process key=" + key + ",value=" + value);
        Stream.of(value.toLowerCase().split(" ")).forEach((String word) -> {
            Optional<Long> counts = Optional.ofNullable(this.kvStore.get(word));
            long count = counts.map(wordcount -> wordcount + 1).orElse(1L);
            //累计存储
            kvStore.put(word, count);
            //传给下游处理器
            context.forward(word, String.valueOf(count));
        });
    }

    @Override
    public void close() {
        // close any resources managed by this processor
        // Note: Do not close any StateStores as these are managed by the library
    }
}