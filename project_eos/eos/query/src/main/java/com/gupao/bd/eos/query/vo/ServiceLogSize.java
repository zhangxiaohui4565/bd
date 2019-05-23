package com.gupao.bd.eos.query.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 日志大小按日期
 */
@Data
@AllArgsConstructor
public class ServiceLogSize {
    private final Date date;
    private final String serviceId;
    private final long size;
}
