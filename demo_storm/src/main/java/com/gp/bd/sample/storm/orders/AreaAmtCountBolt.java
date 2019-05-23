package com.gp.bd.sample.storm.orders;

import com.gp.bd.sample.storm.dao.HbaseDao;
import com.gp.bd.sample.storm.dao.HbaseDaoImpl;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaAmtCountBolt extends BaseBasicBolt {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private String today = "";
    private Map<String, Double> countsMap = null;
    private OutputCollector collector;

    public HashMap<String, Double> initMap() {
        today = formatter.format(new Date());
        HbaseDao hbaseDao = new HbaseDaoImpl();
        HashMap<String, Double> rsltMap = new HashMap<String, Double>();
        List<Result> list = hbaseDao.getRows("demo_storm:storm_area_order", today, new String[]{"order_amt"});
        for (Result rs : list) {
            for (Cell cell : rs.listCells()) {
                String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                if (qualifier.equals("order_amt")) {
                    String rowkey = Bytes.toString(CellUtil.cloneRow(cell));
                    double value = Double.parseDouble(Bytes.toString(CellUtil.cloneValue(cell)));
                    rsltMap.put(rowkey, value);
                }
            }
        }
        return rsltMap;
    }


    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        countsMap = initMap();
        this.collector = collector;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("date_area", "amt"));
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        today = formatter.format(new Date());
        String areaId = (String) input.getValueByField("area_id");
        Double orderAmt = Double.valueOf(input.getValueByField("order_amt") + "");
        String orderDate = (String) input.getValueByField("order_date");
        //跨天清空
        if (!today.equals(orderDate)) {
            countsMap.clear();
        }
        Double count = countsMap.get(orderDate + "_" + areaId);
        if (null == count) {
            count = 0.0;
        }
        count += orderAmt;
        String areaDateKey = orderDate + "_" + areaId;
        System.err.println(">>>>>>areaDateKey" + areaDateKey);
        countsMap.put(areaDateKey, count);
        collector.emit(new Values(areaDateKey, count));
    }
}