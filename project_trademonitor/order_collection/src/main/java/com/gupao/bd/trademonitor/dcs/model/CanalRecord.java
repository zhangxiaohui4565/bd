package com.gupao.bd.trademonitor.dcs.model;

import java.util.Collections;
import java.util.List;

/**
 * 功能：封装从canal server获取的批量binlog日志
 */
public class CanalRecord {

    /**
     * 批次标识符，用于ack/rollback
     */
    private long batchId = -1L;

    private List<RowEntry> rowEntries = Collections.emptyList();

    public CanalRecord(long id) {
        this.batchId = id;
    }

    public boolean isEmpty() {
        return batchId == -1L || rowEntries.isEmpty();
    }

    public long getBatchId() {
        return batchId;
    }

    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }

    public List<RowEntry> getRowEntries() {
        return rowEntries;
    }

    public void setRowEntries(List<RowEntry> rowEntries) {
        this.rowEntries = rowEntries;
    }
}
