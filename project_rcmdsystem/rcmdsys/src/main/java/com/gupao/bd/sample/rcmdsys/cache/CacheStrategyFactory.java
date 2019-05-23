/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author george
 *
 */
@Component
public class CacheStrategyFactory {

    @Autowired
    private NoCacheStrategy noCacheStrategy;
    
    enum CacheStrategyType {
        NO_CACHE
    }
    
    public CacheStrategy getCacheStrategyByType(String type){
        CacheStrategyType strategyType = CacheStrategyType.valueOf(type);
        if(strategyType == null){
            return null;
        }
        switch (strategyType) {
        case NO_CACHE:
            return noCacheStrategy;
        default:
            return null;
        }
    }
}
