package com.gp.bd.sample.storm.orders;

import com.gp.bd.sample.storm.utils.Utils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class AreaOrderTopology {
    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("order-spout",new KafkaSpout(Utils.buildKafkaConfig("area_order")),1);

        builder.setBolt("parser-bolt", new AreaOrderParserBolt(),2)
                .shuffleGrouping("order-spout");
        builder.setBolt("area-amt-count-bolt", new AreaAmtCountBolt(),2)
                .fieldsGrouping("parser-bolt",new Fields("area_id"));
        builder.setBolt("rslt-bolt",new AreaOrderSinkBolt(),1)
                .shuffleGrouping("area-bolt");

        //2、任务提交
        Config config = new Config();
        config.setNumAckers(1);

        StormTopology stormTopology = builder.createTopology();
        if(args != null && args.length > 0){
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        }else{
            //本地模式
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("area_order_topology",config,stormTopology);
        }
    }
}
