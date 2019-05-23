package com.gupao.bd.kafka.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.network.InvalidReceiveException;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * 自定义分区器：将key=1的消息写入到最后一个分区，key<>1的消息写入其他分区
 */
public class CustomerPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();

        if ((keyBytes == null) || !(key instanceof String)) {
            throw new InvalidReceiveException("we expect all messages to have customer id as key");
        }

        if (((String) key).startsWith("1")) {
            return numPartitions - 1;
        }

        return Math.abs(Utils.murmur2(keyBytes)) % (Math.max(numPartitions - 1, 1)); //如果只有一个分区,则所有数据写在一起
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
