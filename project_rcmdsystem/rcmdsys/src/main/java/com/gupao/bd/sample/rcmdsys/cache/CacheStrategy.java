/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.cache;

import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;
import com.gupao.bd.sample.rcmdsys.service.RcmdResult;

/**
 * @author george
 * 缓存策略：缓存推荐数据
 */
public interface CacheStrategy {

    public RcmdResult get(RcmdContext context);
    
    public void put(RcmdResult result, RcmdContext context);
}
