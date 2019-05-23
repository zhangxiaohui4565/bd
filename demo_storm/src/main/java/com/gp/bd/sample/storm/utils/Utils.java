package com.gp.bd.sample.storm.utils;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;

public class Utils {
    public static  void submitTopology(String runEnv, String topologyName, Config config, StormTopology stormTopology) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        if("prod".equals(runEnv)){
            StormSubmitter.submitTopology(topologyName,config,stormTopology);
        } else{
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(topologyName,config,stormTopology);
        }
    }

    public static SpoutConfig buildKafkaConfig(String kafkaTopic){
        BrokerHosts zkHosts = new ZkHosts(PropertiesUtils.getPropertyValue("kafka.zkhosts"));

        final String zkRoot = "";
        final String spoutId = kafkaTopic;

        SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, kafkaTopic, zkRoot, spoutId);
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return kafkaConfig;
    }


    public static TridentKafkaConfig buildTridentKafkaConfig(String kafkaTopic){
        BrokerHosts zkHosts = new ZkHosts(PropertiesUtils.getPropertyValue("kafka.zkhosts"));
        String spoutId = kafkaTopic;

        TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(zkHosts, kafkaTopic,spoutId);
        tridentKafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

        return tridentKafkaConfig;
    }
}
