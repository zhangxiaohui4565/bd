package com.gupao.bd.eos.collector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志存储
 * 实现了visitor模式
 */
public abstract class LogVisitor implements MessageVisitor {
    private final static Logger logger = LoggerFactory.getLogger(LogVisitor.class);

    @Override
    public boolean accept(String message) {
        Log log = null;
        try {
            JSONObject jsonObject = JSON.parseObject(message);
            log = JSON.parseObject(jsonObject.getString("message"), Log.class);
        } catch (Exception e) {
            logger.warn("Failed to pare log from message: " + message, e);
        }
        if (null == log) {
            logger.warn("Failed to pare log from message: {}", message);
        }
        return this.accept(log);
    }

    public abstract boolean accept(Log log);
}
