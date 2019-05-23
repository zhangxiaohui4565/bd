package com.gupao.bd.sample.rcmdsys.conf;

import java.util.List;
import java.util.Map;

import com.gupao.bd.sample.rcmdsys.blacklist.BlacklistRule;
import com.gupao.bd.sample.rcmdsys.controller.RcmdType;

/**
 * @author george
 * 推荐配置：包含所有推荐场景的配置信息和全局的黑名单
 */
public class RcmdSetting {

	private List<BlacklistRule> blacklistRules;
	public Map<RcmdType, Recommender> recommenders;
	
	public Recommender getRecommender(RcmdType rcmdType){
	    return recommenders.get(rcmdType);
	}

    public List<BlacklistRule> getBlacklistRules() {
        return blacklistRules;
    }

    public void setBlacklistRules(List<BlacklistRule> blacklistRules) {
        this.blacklistRules = blacklistRules;
    }

    public Map<RcmdType, Recommender> getRecommenders() {
        return recommenders;
    }

    public void setRecommenders(Map<RcmdType, Recommender> recommenders) {
        this.recommenders = recommenders;
    }
}
