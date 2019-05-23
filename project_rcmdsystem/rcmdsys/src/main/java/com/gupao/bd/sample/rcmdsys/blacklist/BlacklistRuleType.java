/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.blacklist;

/**
 * @author george
 *
 */
public enum BlacklistRuleType {

    FILTER_PORN_MOVIE(new FilterPornMovieRule());
    
    private BlacklistRule instance;
    
    private BlacklistRuleType(BlacklistRule instance){
        this.instance = instance;
    }

    public BlacklistRule getInstance() {
        return instance;
    }
    
    public static BlacklistRule getRuleByType(String type){
        for(BlacklistRuleType rule :BlacklistRuleType.values()){
            if(rule.name().equals(type)){
                return rule.getInstance();
            }
        }
        return null;
    }
}
