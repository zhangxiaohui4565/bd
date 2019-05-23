package com.gupao.bd.eos.query.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 日志大小
 */
@Getter
@Setter
public class ServiceLogSizeResult {
    private String serviceId;
    private long size;
}
