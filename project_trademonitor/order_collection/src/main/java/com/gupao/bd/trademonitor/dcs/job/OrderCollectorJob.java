package com.gupao.bd.trademonitor.dcs.job;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gupao.bd.trademonitor.dcs.dao.HBaseClient;
import com.gupao.bd.trademonitor.dcs.dao.KafkaClient;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能：从Canal Server收集订单及用户基本信息，实时推送至Kafka（不同表对应不同的topic）
 */
public class OrderCollectorJob extends BaseCanalJob {

    private Map<String, String> TOPIC_TABLE_MAPPING;
    private KafkaClient kafkaClient;
    private HBaseClient hBaseClient;

    public OrderCollectorJob(JobConf jobConf) {
        super(jobConf);
        this.kafkaClient = new KafkaClient(jobConf.getKafkaBootstrapServers());
        this.hBaseClient = new HBaseClient(jobConf.getHBaseZKQuorum());
        this.TOPIC_TABLE_MAPPING = jobConf.getKafkaTableTopicMappings();
    }

    @Override
    protected void process(String jsonStr) {
        JSONObject jsonObj = (JSONObject) JSON.parse(jsonStr);
        String tableName = jsonObj.getString("tableName");

        if ("product".equals(tableName)) {
            JSONObject prod = (JSONObject) JSON.parse(jsonObj.getString("after"));
            String rk = prod.getString("id");
            JSONObject record = new JSONObject();
            record.put("p_name",prod.getString("p_name"));
            record.put("p_category",prod.getString("p_category"));

            hBaseClient.put("wuchen:gp_product",rk,record);
        } else {
            kafkaClient.produce(TOPIC_TABLE_MAPPING.get(tableName), jsonStr);
        }
    }

    public static void main(String[] args) {
        Validate.isTrue(ArrayUtils.isNotEmpty(args), "需要通过参数指定配置文件的全路径，配置文件中包含canal server及kafka相关的连接信息");

        JobConf jobConf = new JobConf(args[0]);
        OrderCollectorJob job = new OrderCollectorJob(jobConf);

        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.execute(job);
    }
}
