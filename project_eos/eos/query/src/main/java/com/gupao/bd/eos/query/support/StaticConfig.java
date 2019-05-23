package com.gupao.bd.eos.query.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * jsp文件获取配置
 */
@Component
public class StaticConfig {
    @Value("${static.context.path}")
    private String staticContextPath;

    public String getStaticContextPath() {
        return staticContextPath;
    }
}
