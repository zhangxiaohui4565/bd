/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.shunt;

import org.springframework.stereotype.Component;

/**
 * @author george
 * 分流策略工厂类：根据名称和参数提供分流策略实现类
 */
@Component
public class ShuntStrategyFactory {

    enum ShuntStrategyType {
        SHUNT_BY_MOVIE_ID, POLL_SHUNT
    }
    
    public ShuntStrategy getStrategyByName(String name, String initParam){
        ShuntStrategyType ruleType = ShuntStrategyType.valueOf(name);
        if(ruleType == null){
            return null;
        }
        
        switch (ruleType) {
        case SHUNT_BY_MOVIE_ID:
            return new ShuntByMovieIdStrategy(initParam);
        case POLL_SHUNT:
            return new PollShuntStrategy(initParam);
        default:
            return null;
        }
    }
}
