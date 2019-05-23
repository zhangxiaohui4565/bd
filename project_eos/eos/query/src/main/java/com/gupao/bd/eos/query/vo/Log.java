package com.gupao.bd.eos.query.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 日志字段
 */
@Getter
@Setter
public class Log {
    private int version;
    private String uuid;
    private String traceId;
    private Long timestamp;
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
    private String ip;
    private String hostName;

    private String msgHighlight;
    private String exMsgHighlight;
}
