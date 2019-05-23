package com.gp.bd.sample.storm.trident;

import com.gp.bd.sample.storm.utils.PropertiesUtils;
import com.gp.bd.sample.storm.utils.Utils;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.hbase.trident.mapper.SimpleTridentHBaseMapper;
import org.apache.storm.hbase.trident.mapper.TridentHBaseMapper;
import org.apache.storm.hbase.trident.state.HBaseState;
import org.apache.storm.hbase.trident.state.HBaseStateFactory;
import org.apache.storm.hbase.trident.state.HBaseUpdater;
import org.apache.storm.kafka.trident.OpaqueTridentKafkaSpout;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseAggregator;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.trident.windowing.config.SlidingDurationWindow;
import org.apache.storm.trident.windowing.config.WindowConfig;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.*;

public class WordCountTridentTopology {

    public static class SentenceSplit extends BaseFunction {

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            System.out.println(">>>>"+ tuple.getString(0));
            for(String word: tuple.getString(0).split(" ")) {
                if(word.length() > 0) {
                    collector.emit(new Values(word));
                }
            }
        }

    }


    public static class WordCountAggregate extends BaseAggregator<Map<String, Integer>> {
        @Override
        public Map<String, Integer> init(Object o, TridentCollector tridentCollector) {
            return new HashMap<String, Integer>();
        }

        @Override
        public void aggregate(Map<String, Integer> map, TridentTuple tridentTuple, TridentCollector tridentCollector) {
            String word = tridentTuple.getStringByField("word");
            if (map.containsKey(word)) {
                int num = map.get(word);
                map.put(word, num + 1);
            } else {
                map.put(word, 1);
            }
        }

        @Override
        public void complete(Map<String, Integer> map, TridentCollector tridentCollector) {
            List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue() - o1.getValue();
                }
            });
            for (int i = 0; i < 5 && i < list.size(); i++) {
                tridentCollector.emit(new Values(String.valueOf(i + 1), list.get(i).getKey(), list.get(i).getValue().toString()));
            }
        }
    }


    /**
     * 转换成小写
     */
    public static class LowercaseFunction extends BaseFunction {

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            collector.emit(new Values(tuple.getString(0).toLowerCase()));
        }
    }



    public static void main(String[] args) {

        String kafkaTopic = PropertiesUtils.getPropertyValue("kafka.topic");
        OpaqueTridentKafkaSpout kafkaSpout = new OpaqueTridentKafkaSpout(Utils.buildTridentKafkaConfig(kafkaTopic));

        Config conf = new Config();
        Map<String, String> HBASE_CONFIG = new HashMap<>();
        HBASE_CONFIG.put("hbase.zookeeper.quorum", "localhost");
        HBASE_CONFIG.put("hbase.zookeeper.property.clientPort", "2181");
        conf.put("hbase_config", HBASE_CONFIG);

        StateFactory factory = buildHBaseStateFactory();

        WindowConfig durationWindow = SlidingDurationWindow.of(BaseWindowedBolt.Duration.seconds(10), BaseWindowedBolt.Duration.seconds(5));

        TridentTopology topology = new TridentTopology();
        System.out.println(kafkaSpout.getOutputFields().toList().toString());
        Stream wcTridentStream =  topology.newStream("wc_trident_steam", kafkaSpout);

        wcTridentStream
                .parallelismHint(1)
                .shuffle()
                .each(new Fields("str"),new SentenceSplit(), new Fields("item"))
                .parallelismHint(2)
                .each(new Fields("item"), new LowercaseFunction(), new Fields("word"))
                .parallelismHint(2)
                .window(durationWindow, new Fields("word"), new WordCountAggregate(), new Fields("rank", "word", "count"))
                .partitionPersist(factory, new Fields("rank", "word", "count"), new HBaseUpdater(), new Fields());


        StormTopology stormTopology = topology.build();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("storm_trident_wc", conf, stormTopology);
    }


    public static StateFactory buildHBaseStateFactory(){
        TridentHBaseMapper tridentHBaseMapper = new SimpleTridentHBaseMapper()
                .withColumnFamily("info")
                .withColumnFields(new Fields("count", "word"))
                .withRowKeyField("rank");

        HBaseState.Options options = new HBaseState.Options()
                .withConfigKey("hbase_config")
                .withMapper(tridentHBaseMapper)
                .withTableName("demo_storm:storm_trident_wc");

        return new HBaseStateFactory(options);
    }



}
