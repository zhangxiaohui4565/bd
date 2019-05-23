package com.gupao.bd.eos.collector;

import org.apache.commons.cli.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 日志搜集/消费启动类
 *
 * 连接kafka消费者和es索引
 *
 */
public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    private final static String LOG_KAFKA_PREFIX = "log.kafka";
    private final static String LOG_KAFKA_SERVERS = "log.kafka.servers";
    private final static String LOG_KAFKA_TOPIC = "log.kafka.topic";
    private final static String LOG_KAFKA_CONSUMER_GROUP_ID = "log.kafka.consumer.group.id";

    private final static List<String> LOG_KAFKA_REQUIRED_KEYS = Arrays.asList(
            LOG_KAFKA_SERVERS,
            LOG_KAFKA_TOPIC,
            LOG_KAFKA_CONSUMER_GROUP_ID
    );

    public static void main(String[] args) {
        // 命令行参数解析
        Option configOpt = new Option("c", "config", true, "config file");
        configOpt.setRequired(true);
        Option logPropsOpt = new Option("l", "logConfig", true, "log4j config file");
        logPropsOpt.setRequired(true);
        Option helpOpt = new Option("h", "help", false, "show help");
        helpOpt.setRequired(false);

        Options options = new Options();
        options.addOption(configOpt);
        options.addOption(logPropsOpt);
        options.addOption(helpOpt);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            System.out.println("Parse arguments error: " + e.getMessage());
            formatter.printHelp("log-collector", options);
            System.exit(1);
        }
        String configFile = cmd.getOptionValue("c");
        if (null == configFile) {
            System.out.println("Arguments error, missing config file");
            formatter.printHelp("log-collector", options);
            System.exit(1);
        }

        String log4jConfFile = cmd.getOptionValue("l");
        if (null == log4jConfFile) {
            System.out.println("Arguments error, missing log4j config file");
            formatter.printHelp("log-collector", options);
            System.exit(1);
        }

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(log4jConfFile));
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(properties);
        } catch (IOException e) {
            System.out.println("Arguments error, failed to read log4j config file");
            formatter.printHelp("log-collector", options);
            System.exit(1);
        }

        // 解析配置文件
        Properties properties = null;
        try {
            FileInputStream inputStream = new FileInputStream(configFile);
            properties = new Properties();
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            System.err.println("Config file " + configFile + " is not found");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to read config file " + configFile);
            System.exit(1);
        }

        // es连接参数
        String esCluster = properties.getProperty("es.cluster.name");
        String esTransportsStr = properties.getProperty("es.transport.addresses");
        String[] esTransports = esTransportsStr.split(",");

        // es日志写入类初始化
        LogEsVisitor logEsVisitor = new LogEsVisitor(esCluster, esTransports);
        String maxPollLogCount = properties.getProperty("log.kafka.max.poll.records");
        logEsVisitor.setBatchSize(Integer.parseInt(maxPollLogCount));
        logEsVisitor.init();
        logger.info("LogEsVisitor initialized");

        // kafka配置
        String kafkaServer = properties.getProperty(LOG_KAFKA_SERVERS);
        String topic = properties.getProperty(LOG_KAFKA_TOPIC);
        String groupId = properties.getProperty(LOG_KAFKA_CONSUMER_GROUP_ID);

        Properties optionalLogKafkaProps = new Properties();
        properties.forEach((key, value) -> {
            String keystr = key.toString();
            // kafka 附件参数
            if (keystr.startsWith(LOG_KAFKA_PREFIX) && isNotRequiredKafkaLogConf(keystr)) {
                String kafkaProp = keystr.substring(LOG_KAFKA_PREFIX.length());
                optionalLogKafkaProps.put(kafkaProp, value);
            }
        });

        // 初始化日志消费者
        LogConsumer logConsumer = new LogConsumer(kafkaServer, topic, groupId, logEsVisitor);
        logConsumer.setProperties(optionalLogKafkaProps);
        logConsumer.init();
        logger.info("LogConsumer initialized");
        logConsumer.start();

        // 停止hook，停止时关闭连接，清理资源
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Program exiting...");
                logConsumer.stop();
                logConsumer.destroy();
                logger.info("LogConsumer stopped");
                logEsVisitor.destroy();
                logger.info("LogEsVisitor stopped");
                logger.info("Program exit");
            }
        });
    }

    /**
     * 是否是必须的kafka连接参数
     * @param key
     * @return
     */
    private static boolean isNotRequiredKafkaLogConf(String key) {
        return !LOG_KAFKA_REQUIRED_KEYS.contains(key);
    }
}
