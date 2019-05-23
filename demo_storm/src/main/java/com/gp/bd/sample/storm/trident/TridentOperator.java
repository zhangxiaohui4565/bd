package com.gp.bd.sample.storm.trident;

import clojure.lang.Numbers;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.*;
import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.IOException;
import java.util.Map;

public class TridentOperator {

    /**
     * 校验是否是偶数的过滤器
     */
    public static class CheckEvenFilter extends BaseFilter {
        int partitionIndex;

        @Override
        public void prepare(Map conf, TridentOperationContext context) {
            this.partitionIndex = context.getPartitionIndex();
        }

        public boolean isKeep(TridentTuple tuple) {
            System.out.println("》》》》》》partitionIndex:"+partitionIndex +" tuple:"+tuple);
            return tuple.getInteger(0) % 2 == 0;
        }
    }




    /**
     * 求和函数
     */
    public static  class SumFunction extends BaseFunction {
        @Override
        public void execute(TridentTuple input, TridentCollector collector) {
            Integer num1 = input.getInteger(0);
            Integer num2 = input.getInteger(1);
            int sum = num1 + num2;
            collector.emit(new Values(sum));
        }

    }


    /**
     * 统计条目数
     */
    public static class Count implements CombinerAggregator<Long> {
        @Override
        public Long init(TridentTuple tuple) {
            return 1L;
        }

        @Override
        public Long combine(Long val1, Long val2) {
            return val1 + val2;
        }

        @Override
        public Long zero() {
            return 0L;
        }
    }


    /**
     * 条目数求和
     */
    public static class Sum implements CombinerAggregator<Number> {
        public Sum() {
        }

        public Number init(TridentTuple tuple) {
            return (Number)tuple.getValue(0);
        }

        public Number combine(Number val1, Number val2) {
            return Numbers.add(val1, val2);
        }

        public Number zero() {
            return 0;
        }
    }



    /**
     *  打印
     */
    public static class Printer extends  BaseFilter {

        @Override
        public boolean isKeep(TridentTuple tuple) {
            System.out.println(">>>>>>"+tuple);
            return true;
        }
    }

    public class PrintFunction extends BaseFunction {

        @Override
        public void execute(TridentTuple input, TridentCollector collector) {
            Integer sum = input.getInteger(0);
            System.out.println(this.getClass().getCanonicalName() + ": " + sum);
        }

    }


    public static StormTopology buildTopology() throws IOException {

        // 创建 trident topology
        TridentTopology topology = new TridentTopology();


        // 创建spout

        FixedBatchSpout testSpout = new FixedBatchSpout(new Fields("a", "b"), 3,
                new Values(1, 1),
                new Values(1, 5),
                new Values(2, 1),
                new Values(3, 5),
                new Values(4, 1),
                new Values(5, 5),
                new Values(6, 1)
        );
        testSpout.setCycle(true);
        // 创建流
        Stream stream = topology.newStream("spout", testSpout);
        // 过滤操作

        stream
                .shuffle()
                .each(new Fields("b"), new CheckEvenFilter())
                .parallelismHint(2)
                .each(new Fields("a", "b"),new Printer());

        stream
                .each(new Fields("a", "b"), new SumFunction(), new Fields("d"))
                .each(new Fields("d"),new Printer());

//
        // 聚合操作
        stream
                .each(new Fields("a", "b"), new SumFunction(), new Fields("d"))
                .aggregate(new Fields("d"),new Count(), new Fields("count"))
                .each(new Fields("count"),new Printer());
//
        // 分区聚合操作
        stream
                .each(new Fields("a", "b"), new SumFunction(), new Fields("d"))
                .partitionAggregate(new Fields("d"),new Sum(), new Fields("count"))
                .each(new Fields("count"),new Printer());
//
//        // 持久化聚合操作 - 保存统计值到内存中
        stream
                .each(new Fields("a", "b"), new SumFunction(), new Fields("d"))
                .persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"));
//
//        // 分组-持久化到内存
        stream
                .each(new Fields("a", "b"), new SumFunction(), new Fields("d"))
                .groupBy(new Fields("d"))
                .persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))
                .newValuesStream().shuffle()
                .each(new Fields("d", "count"), new Printer());

        return topology.build();
    }


    public static void main(String[] args) throws IOException {

        Config conf = new Config();
        conf.setMaxSpoutPending(2000);
        conf.setNumWorkers(2);
        conf.setMessageTimeoutSecs(100000);
        LocalCluster local = new LocalCluster();
        local.submitTopology("trident-test-topology", conf, buildTopology());
    }

}
