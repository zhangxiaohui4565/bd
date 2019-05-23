package com.gupao.bd.eos.query.support;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * ES客户端
 */
@Component
public class ESClient {
    private final String clusterName;
    private final String esTransportAddresses;
    private final String indexPrefix;
    private final String indexType;

    private TransportClient esClient;

    @Autowired
    public ESClient(@Value("${es.cluster.name}") String clusterName,
                    @Value("${es.transport.addresses}") String esTransportAddresses,
                    @Value("${es.log.index.prefix:service_log}") String indexPrefix,
                    @Value("${es.log.index.type:all}") String indexType) {
        this.clusterName = clusterName;
        this.esTransportAddresses = esTransportAddresses;
        this.indexPrefix = indexPrefix;
        this.indexType = indexType;
    }

    @PostConstruct
    public void init() {
        Settings settings = Settings.builder()
                .put("cluster.name", this.clusterName)
                .build();
        esClient = new PreBuiltTransportClient(settings);
        List<String> addresses = Arrays.asList(esTransportAddresses.split(","));
        addresses.forEach(esNode -> {
            String[] node = esNode.split(":");
            try {
                esClient.addTransportAddress(new TransportAddress(InetAddress.getByName(node[0]),
                        Integer.parseInt(node[1])));
            } catch (UnknownHostException e) {
                throw new RuntimeException("Bad ES node config", e);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        if (null != esClient) {
            esClient.close();
        }
    }

    public TransportClient get() {
        return esClient;
    }

    public String getIndexPrefix() {
        return indexPrefix;
    }

    public String getIndexType() {
        return indexType;
    }
}
