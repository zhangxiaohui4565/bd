package com.gupao.bd.trademonitor.dcs.model;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 功能：封装从canal server获取的单条binlog信息
 */
public class RowEntry {

    private long eventId = 0;
    private String eventType;
    private String eventTime;
    private String binlogName;
    private long position = 0;
    private long serverId = 0;
    private String databaseName;
    private String tableName;
    private Map<String, String> before = null;
    private Map<String, String> after = null;
    private Map<String, String> data = null;

    private Boolean ddl;
    private String sql = null;

    public RowEntry(CanalEntry.Header header) {
        this.serverId = header.getServerId();
        this.binlogName = header.getLogfileName();
        this.eventTime = DateFormatUtils.format(header.getExecuteTime(), "yyyy-MM-dd HH:mm:ss.SSS");
        this.position = header.getLogfileOffset();
        this.databaseName = header.getSchemaName();
        this.tableName = header.getTableName();
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getBinlogName() {
        return binlogName;
    }

    public void setBinlogName(String binlogName) {
        this.binlogName = binlogName;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getBefore() {
        return before;
    }

    public void setBefore(Map<String, String> before) {
        this.before = before;
    }

    public Map<String, String> getAfter() {
        return after;
    }

    public void setAfter(Map<String, String> after) {
        this.after = after;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Boolean isDdl() {
        return ddl;
    }

    public void setDdl(Boolean ddl) {
        this.ddl = ddl;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Boolean getDdl() {
        return ddl;
    }

    public String toJsonString() {
        return JSON.toJSONString(this, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }

}