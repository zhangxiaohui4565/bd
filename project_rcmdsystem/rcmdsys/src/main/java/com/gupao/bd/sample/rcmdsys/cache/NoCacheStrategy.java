/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.cache;

import org.springframework.stereotype.Component;

import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;
import com.gupao.bd.sample.rcmdsys.service.RcmdResult;

/**
 * @author george
 *
 */
@Component
public class NoCacheStrategy implements CacheStrategy {

    @Override
    public RcmdResult get(RcmdContext context) {
        // 不做处理
        return null;
    }

    @Override
    public void put(RcmdResult result, RcmdContext context) {
        // 不做处理
    }

}
