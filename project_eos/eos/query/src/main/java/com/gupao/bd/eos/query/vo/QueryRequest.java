package com.gupao.bd.eos.query.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * 查询请求
 */
@Getter
@Setter
public class QueryRequest {
    private String serviceId;
    private Long startTime;
    private Long endTime;

    private String queryString;
    private String level;
    private String ip;

    private Boolean orderByTimeDesc = true;

    private int page = 0;
    private int pageSize = 50;

    public void validate() {
        if (!StringUtils.hasText(serviceId)) {
            throw new IllegalArgumentException("Required param serviceId missing");
        }
        if (null == startTime) {
            throw new IllegalArgumentException("Required param startTime missing");
        }
        if (null == endTime) {
            throw new IllegalArgumentException("Required param endTime missing");
        }
    }
}
