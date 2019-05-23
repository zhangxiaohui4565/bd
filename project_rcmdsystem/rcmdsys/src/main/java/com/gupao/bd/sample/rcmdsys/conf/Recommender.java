/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.conf;

import java.util.List;

import com.gupao.bd.sample.rcmdsys.algorithm.RcmdAlgorithm;
import com.gupao.bd.sample.rcmdsys.bizrule.BizRule;
import com.gupao.bd.sample.rcmdsys.cache.CacheStrategy;
import com.gupao.bd.sample.rcmdsys.shunt.ShuntStrategy;

/**
 * @author george
 *
 */
public class Recommender {

    private CacheStrategy cacheStrategy;
    private ShuntStrategy shuntStrategy;
    private RcmdAlgorithm defaultAlgorithm;
    private List<BizRule> bizRules;

    public void validate() {
        if (defaultAlgorithm == null) {
            throw new RuntimeException("默认算法不能为空");
        }
    }

    public CacheStrategy getCacheStrategy() {
        return cacheStrategy;
    }

    public void setCacheStrategy(CacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    public ShuntStrategy getShuntStrategy() {
        return shuntStrategy;
    }

    public void setShuntStrategy(ShuntStrategy shuntStrategy) {
        this.shuntStrategy = shuntStrategy;
    }

    public RcmdAlgorithm getDefaultAlgorithm() {
        return defaultAlgorithm;
    }

    public void setDefaultAlgorithm(RcmdAlgorithm defaultAlgorithm) {
        this.defaultAlgorithm = defaultAlgorithm;
    }

    public List<BizRule> getBizRules() {
        return bizRules;
    }

    public void setBizRules(List<BizRule> bizRules) {
        this.bizRules = bizRules;
    }

}
