package com.gupao.bd.eos.collector;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 日志消费客户端
 *
 * 从kafka消费日志，输出到messageVisitor
 */
public class LogConsumer {
    private final static Logger logger = LoggerFactory.getLogger(LogConsumer.class);

    private final String topic;
    private Properties properties;

    private volatile boolean shutdown;

    private KafkaConsumer<String, String> consumer;

    private MessageVisitor messageVisitor;

    /**
     * 构造器
     * 包含kafka启动必须的属性
     *
     * @param bootstrupServers
     * @param topic
     * @param groupId
     * @param messageVisitor
     */
    public LogConsumer(String bootstrupServers, String topic, String groupId, MessageVisitor messageVisitor) {
        this.topic = topic;
        this.messageVisitor = messageVisitor;
        properties = new Properties();
        properties.put("bootstrap.servers", bootstrupServers);
        properties.put("group.id", groupId);
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
    }

    /**
     * 设置kafka扩展属性
     * @param properties
     */
    public void setProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    /**
     * 初始化
     * 订阅topic
     */
    public void init() {
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topic));
    }

    /**
     * 启动消费
     */
    public void start() {
        while (!shutdown) {
            try {
                // poll记录，超时600ms
                ConsumerRecords<String, String> records = consumer.poll(600);
                AtomicInteger accepted = new AtomicInteger(0);
                records.forEach(record -> {
                    try {
                        String message = record.value();
                        if (logger.isTraceEnabled()) {
                            logger.trace("Received message from topic: {}, key: {}, message: {}",
                                    record.topic(), record.key(), message);
                        }
                        // visitor模式，处理记录
                        if (messageVisitor.accept(message)) {
                            accepted.incrementAndGet();
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to parse message: " + record, e);
                    }
                });
                if (accepted.get() > 0) {
                    // 批量刷新日志到后端存储
                    boolean flushed = messageVisitor.flush();
                    if (flushed) {
                        // 成功则提交offset
                        consumer.commitSync();
                    } else {
                        // 不可恢复错误则停止消费，需要人工介入
                        logger.warn("Failed to flush data, stop consumer");
                        this.shutdown = true;
                    }
                }
            } catch (WakeupException e) {
                // ignore exit excepiton
                logger.info("Catches WakeupException, topic: {}, program is exiting", topic);
            } catch (Exception e) {
                // 不可恢复错误则停止消费，需要人工介入
                logger.warn("Unexpected exception happened, topic: " + topic, e);
                shutdown = true;
            }
        }
    }

    /**
     * 停止消费
     * 关闭消费者
     */
    public void stop() {
        shutdown = true;
        consumer.wakeup();
        try {
            consumer.close();
        } catch (Exception e) {
            logger.warn("Failed close consumer, topic:" + topic, e);
        }
    }

    public void destroy() {
        // nothing to do
    }
}
