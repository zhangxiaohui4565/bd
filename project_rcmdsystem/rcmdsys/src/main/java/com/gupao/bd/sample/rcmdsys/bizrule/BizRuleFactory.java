/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.bizrule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author george
 *
 */
@Component
public class BizRuleFactory {

    enum BizRuleType {
        ADD_FEATURED_MOVIE
    }
    
    @Autowired
    private AddFeaturedMoviesRule addFeaturedMoviesRule;
    
    public BizRule getRuleByType(String typeName, String initParam){
        BizRuleType ruleType = BizRuleType.valueOf(typeName);
        if(ruleType == null){
            return null;
        }
        
        switch (ruleType) {
        case ADD_FEATURED_MOVIE:
            addFeaturedMoviesRule.loadInitParam(initParam);
            return addFeaturedMoviesRule;
        default:
            return null;
        }
    }
}
