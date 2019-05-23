package com.gupao.bd.kafka_streams.demo.order_analysis;

import com.gupao.bd.kafka_streams.demo.BaseExample;
import com.gupao.bd.kafka_streams.demo.order_analysis.model.*;
import com.gupao.bd.kafka_streams.serdes.SerdesFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.io.IOException;
import java.util.Properties;


/**
 * 统计商品产地与客户收货地相同的订单金额,并按产地分组
 */
public class PurchaseAnalysis extends BaseExample {

    public static void main(String[] args) throws IOException, InterruptedException {

        //1、设置配置参数
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "PurchaseAnalysis7");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, OrderTimestampExtractor.class);

        //2、构造计算topology
        StreamsBuilder streamBuilder = new StreamsBuilder();

        KStream<String, Order> orderStream = streamBuilder.stream("gp_orders", Consumed.with(Serdes.String(), SerdesFactory.serdFrom(Order.class)));
        KTable<String, User> userTable = streamBuilder.table("gp_users", Consumed.with(Serdes.String(), SerdesFactory.serdFrom(User.class)), Materialized.as("users-state-store1"));
        KTable<String, Item> itemTable = streamBuilder.table("gp_items", Consumed.with(Serdes.String(), SerdesFactory.serdFrom(Item.class)), Materialized.as("items-state-store1"));

        orderStream
                .leftJoin(userTable, (Order order, User user) -> OrderUser.fromOrderUser(order, user), Joined.with(Serdes.String(), SerdesFactory.serdFrom(Order.class), SerdesFactory.serdFrom(User.class)))
                .filter((String userName, OrderUser orderUser) -> orderUser.getUserAddress() != null)
                .map((String userName, OrderUser orderUser) -> new KeyValue<>(orderUser.getItemName(), orderUser))
                .through("gp_orderuser-repartition-by-item", Produced.with(Serdes.String(), SerdesFactory.serdFrom(OrderUser.class), (String topic, String key, OrderUser orderUser, int numPartitions) -> (orderUser.getItemName().hashCode() & 0x7FFFFFFF) % numPartitions))
                .leftJoin(itemTable, (OrderUser orderUser, Item item) -> OrderUserItem.fromOrderUser(orderUser, item), Joined.with(Serdes.String(), SerdesFactory.serdFrom(OrderUser.class), SerdesFactory.serdFrom(Item.class)))
                .filter((String item, OrderUserItem orderUserItem) -> StringUtils.equals(orderUserItem.getUserAddress(), orderUserItem.getItemAddress()))
                .map((String item, OrderUserItem orderUserItem) -> new KeyValue<>(orderUserItem.getItemAddress(), orderUserItem.getQuantity() * orderUserItem.getItemPrice()))
                .groupByKey(Serialized.with(Serdes.String(), Serdes.Double()))
                .reduce((Double v1, Double v2) -> v1 + v2, Materialized.as("item-amount-state-store1"))
                .toStream().map((String itemName, Double amount) -> new KeyValue<>(itemName, String.valueOf(amount))).to("gp_item-amount");

        //3、启动程序
        KafkaStreams kafkaStreams = new KafkaStreams(streamBuilder.build(), props);
        kafkaStreams.start();
    }
}
