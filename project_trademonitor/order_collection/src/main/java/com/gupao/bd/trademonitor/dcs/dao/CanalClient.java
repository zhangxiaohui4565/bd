package com.gupao.bd.trademonitor.dcs.dao;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.gupao.bd.trademonitor.dcs.job.JobConf;
import com.gupao.bd.trademonitor.dcs.model.CanalRecord;
import com.gupao.bd.trademonitor.dcs.model.RowEntry;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * 功能：从canal server获取解析后的binlog
 */
public class CanalClient {

    private CanalConnector connector;

    public CanalClient(JobConf conf) {
        this.connector = CanalConnectors.newSingleConnector(new InetSocketAddress(conf.getCanalServerHost(),
                conf.getCanalServerPort()), conf.getCanalDestination(), "", "");
    }

    public void subscribe(String filter) {
        connector.connect();
        connector.subscribe(filter);
    }

    public CanalRecord poll() {
        return poll(100);
    }

    public CanalRecord poll(int batchSize) {
        Message message = connector.getWithoutAck(batchSize);
        long batchId = message.getId();
        int size = message.getEntries().size();

        CanalRecord consumerRecord = new CanalRecord(batchId);

        if (batchId != -1 && size > 0) {
            List<RowEntry> rowEntries = parseEntry(message.getEntries());
            consumerRecord.setRowEntries(rowEntries);
        }

        return consumerRecord;
    }

    public void close() {
        connector.disconnect();
    }

    public void ack(long batchId) {
        connector.ack(batchId);
    }

    public void rollback(long batchId) {
        connector.rollback(batchId);
    }

    private List<RowEntry> parseEntry(List<CanalEntry.Entry> rawEntries) {
        List<RowEntry> rowEntries = new ArrayList<>();

        //多条变更日志
        for (CanalEntry.Entry entry : rawEntries) {

            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange;

            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                continue;
            }

            CanalEntry.EventType eventType = rowChange.getEventType();

            CanalEntry.Header header = entry.getHeader();

            System.out.println(String.format("================ binlog[%s:%s] , tableName[%s.%s] , eventType : %s",
                    header.getLogfileName(), header.getLogfileOffset(),
                    header.getSchemaName(), header.getTableName(),
                    eventType));

            RowEntry rowEntry = new RowEntry(header);
            rowEntry.setEventType(eventType.name());
            rowEntry.setDdl(rowChange.getIsDdl());
            rowEntry.setSql(rowChange.getSql());

            //多条变更记录
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {

                if (eventType == CanalEntry.EventType.DELETE) {
                    Map<String, String> rowMap = parseColumn(rowData.getBeforeColumnsList(), "before");
                    rowEntry.setBefore(rowMap);

                    rowEntry.setData(rowMap);
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    Map<String, String> rowMap = parseColumn(rowData.getAfterColumnsList(), "after");
                    rowEntry.setAfter(rowMap);

                    rowEntry.setData(rowMap);
                } else if (eventType == CanalEntry.EventType.UPDATE) {
                    Map<String, String> rowMap = parseColumn(rowData.getBeforeColumnsList(), "before");
                    rowEntry.setBefore(rowMap);

                    Map<String, String> rowMap2 = parseColumn(rowData.getAfterColumnsList(), "after");
                    rowEntry.setAfter(rowMap2);

                    rowEntry.setData(rowMap2);
                } else {
                    System.out.println("ignore EventType:" + eventType);
                }

                rowEntries.add(rowEntry);
            }
        }
        return rowEntries;
    }

    private Map<String, String> parseColumn(List<CanalEntry.Column> columns, String beforeOrAfter) {
        Map<String, String> data = new HashMap<>(columns.size());
        System.out.println("--------------------- " + beforeOrAfter + " ----------------------");
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
            data.put(column.getName(), column.getValue());
        }
        return data;
    }
}
