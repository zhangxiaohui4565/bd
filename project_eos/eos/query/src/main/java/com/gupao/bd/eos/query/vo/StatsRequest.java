package com.gupao.bd.eos.query.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 统计结果请求
 */
@Getter
@Setter
public class StatsRequest {
    private Long startTime;
    private Long endTime;

    private String firstGroupField;
    private String secondGroupField;

    public void validate() {
        if (null == startTime) {
            throw new IllegalArgumentException("Required param startTime missing");
        }
        if (null == endTime) {
            throw new IllegalArgumentException("Required param endTime missing");
        }
    }
}
