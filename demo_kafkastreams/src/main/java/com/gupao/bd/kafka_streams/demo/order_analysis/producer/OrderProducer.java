package com.gupao.bd.kafka_streams.demo.order_analysis.producer;

import com.gupao.bd.kafka_streams.demo.BaseExample;
import com.gupao.bd.kafka_streams.demo.order_analysis.model.Order;
import com.gupao.bd.kafka_streams.serdes.GenericSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class OrderProducer extends BaseExample {

    private static DateTimeFormatter dataTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers",KAFKA_BOOTSTRAP_SERVERS);
        props.put("acks", "all");
        props.put("retries", 3);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", GenericSerializer.class.getName());
        props.put("value.serializer.type", Order.class.getName());
        props.put("partitioner.class", HashPartitioner.class.getName());

        Producer<String, Order> producer = new KafkaProducer<>(props);
        List<Order> orders = readOrder();
        orders.forEach((Order order) -> producer.send(new ProducerRecord<String, Order>("gp_orders", order.getUserName(), order)));
        producer.close();
    }

    public static List<Order> readOrder() throws IOException {
        InputStream inputStream = OrderProducer.class.getResourceAsStream("/orders.csv");
        List<String> lines = IOUtils.readLines(inputStream, Charset.forName("UTF-8"));
        List<Order> orders = lines.stream()
                .filter(StringUtils::isNotBlank)
                .map((String line) -> line.split("\\s*,\\s*"))
                .filter((String[] values) -> values.length == 4)
                .map((String[] values) -> new Order(values[0], values[1], LocalDateTime.parse(values[2], dataTimeFormatter).toEpochSecond(ZoneOffset.UTC) * 1000, Integer.parseInt(values[3])))
                .collect(Collectors.toList());
        return orders;
    }

}
