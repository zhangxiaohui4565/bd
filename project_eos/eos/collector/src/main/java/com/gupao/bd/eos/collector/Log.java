package com.gupao.bd.eos.collector;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

/**
 * 日志字段
 */
@Getter
@Setter
public class Log {
    // 日志元数据
    /**
     * 日志版本，用于多版本兼容
     */
    private int version;
    /**
     * 唯一id，防止重复
     */
    private String uuid;
    /**
     * 关联日志id
     */
    private String traceId;
    private Long timestamp;
    private String ip;
    private String hostName;
    private String serviceId;

    // 日志原始字段
    private String threadName;
    private String threadId;
    private String level;
    private int line;
    private String clazz;
    private String method;
    private String message;
    private String exceptionClass;
    private String exceptionMessage;
    private String exceptionStack;

    public String toJSON() {
        return JSON.toJSONString(this);
    }
}
