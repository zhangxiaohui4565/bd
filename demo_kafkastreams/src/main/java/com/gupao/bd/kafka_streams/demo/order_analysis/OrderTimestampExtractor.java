package com.gupao.bd.kafka_streams.demo.order_analysis;


import com.fasterxml.jackson.databind.JsonNode;
import com.gupao.bd.kafka_streams.demo.order_analysis.model.Item;
import com.gupao.bd.kafka_streams.demo.order_analysis.model.Order;
import com.gupao.bd.kafka_streams.demo.order_analysis.model.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class OrderTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        Object value = record.value();
        if (record.value() instanceof Order) {
            Order order = (Order) value;
            return order.getTransactionDate();
        }
        if (value instanceof JsonNode) {
            return ((JsonNode) record.value()).get("transactionDate").longValue();
        }
        if (value instanceof Item) {
            return LocalDateTime.of(2015, 12, 11, 1, 0, 10).toEpochSecond(ZoneOffset.UTC) * 1000;
        }
        if (value instanceof User) {
            return LocalDateTime.of(2015, 12, 11, 0, 0, 10).toEpochSecond(ZoneOffset.UTC) * 1000;
        }
        return LocalDateTime.of(2015, 11, 10, 0, 0, 10).toEpochSecond(ZoneOffset.UTC) * 1000;
    }
}
