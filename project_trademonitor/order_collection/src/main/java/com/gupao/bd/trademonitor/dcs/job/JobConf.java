package com.gupao.bd.trademonitor.dcs.job;


import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * 功能：连接canal server及kafka broker相关的配置信息
 */
public class JobConf {

    private static final String KEY_CANAL_SERVER_HOST = "canal.server.host";
    private static final String KEY_CANAL_SERVER_PORT = "canal.server.port";
    private static final String KEY_CANAL_SERVER_DESTINATION = "canal.server.destination";
    private static final String KEY_CANAL_TABLE_FILTER = "canal.table.filter";
    private static final String KEY_KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String KEY_KAFKA_TABLE_TOPIC_MAPPINGS = "kafka.table.topic.mappings";
    private static final String KEY_HBASE_ZK_QUORUM = "hbase.zookeeper.quorum";

    private Properties prop = new Properties();

    public JobConf(String fileName) {
        File file = new File(fileName);

        try (FileInputStream fis = new FileInputStream(file)) {
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getKafkaTableTopicMappings() {
        String str = StringUtils.trim(prop.getProperty(KEY_KAFKA_TABLE_TOPIC_MAPPINGS));
        String[] array = str.split(",");
        Map<String, String> tableTopicMap = new HashedMap();
        for (String entry : array) {
            String[] kv = StringUtils.trim(entry).split("->");
            tableTopicMap.put(StringUtils.trim(kv[0]), StringUtils.trim(kv[1]));
        }
        return tableTopicMap;
    }

    public String getKafkaBootstrapServers() {
        return prop.getProperty(KEY_KAFKA_BOOTSTRAP_SERVERS);
    }

    public String getCanalTableFilter() {
        return prop.getProperty(KEY_CANAL_TABLE_FILTER);
    }

    public String getCanalServerHost() {
        return prop.getProperty(KEY_CANAL_SERVER_HOST);
    }

    public int getCanalServerPort() {
        return Integer.valueOf(prop.getProperty(KEY_CANAL_SERVER_PORT));
    }

    public String getCanalDestination() {
        return prop.getProperty(KEY_CANAL_SERVER_DESTINATION);
    }

    public String getHBaseZKQuorum() {
        return prop.getProperty(KEY_HBASE_ZK_QUORUM);
    }

}
