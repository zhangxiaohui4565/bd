package com.gupao.bd.eos.collector;

/**
 * 日志处理接口
 */
public interface MessageVisitor {
    boolean accept(String message);
    boolean flush();
}
