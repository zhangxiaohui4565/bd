/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gupao.bd.sample.rcmdsys.algorithm.RcmdAlgorithm;
import com.gupao.bd.sample.rcmdsys.algorithm.RcmdAlgorithmFactory;
import com.gupao.bd.sample.rcmdsys.bizrule.BizRule;
import com.gupao.bd.sample.rcmdsys.bizrule.BizRuleFactory;
import com.gupao.bd.sample.rcmdsys.blacklist.BlacklistRule;
import com.gupao.bd.sample.rcmdsys.blacklist.BlacklistRuleType;
import com.gupao.bd.sample.rcmdsys.cache.CacheStrategy;
import com.gupao.bd.sample.rcmdsys.cache.CacheStrategyFactory;
import com.gupao.bd.sample.rcmdsys.controller.RcmdType;
import com.gupao.bd.sample.rcmdsys.shunt.ShuntStrategy;
import com.gupao.bd.sample.rcmdsys.shunt.ShuntStrategyFactory;
import com.gupao.bd.sample.rcmdsys.util.JsonHelper;

/**
 * @author george
 * 推荐配置加载器
 */
@Component
public class RcmdSettingLoader {

    @Autowired
    private RcmdAlgorithmFactory algorithmFactory;
    @Autowired
    private CacheStrategyFactory cacheStrategyFactory;
    @Autowired
    private BizRuleFactory bizRuleFactory;
    @Autowired
    private ShuntStrategyFactory shuntStrategyFactory;
    
    private RcmdSetting rcmdSetting;
    
    public RcmdSetting getRcmdSetting() {
        return rcmdSetting;
    }
    
    public void loadRcmdSetting(String settingConfPath) throws Exception{
        String confStr = FileUtils.readFileToString(new File(settingConfPath));
        rcmdSetting = new RcmdSetting();
        Map<String, Object> confObjs = JsonHelper.fromJson(confStr, Map.class);
        for(String confKey : confObjs.keySet()){
            Object confContent = confObjs.get(confKey);
            if("blacklistRules".equals(confKey)){
                // 加载黑名单设置
                rcmdSetting.setBlacklistRules(loadBlacklistRules(confContent));
            } else if("recommenders".equals(confKey)){
                // 加载推荐配置
                rcmdSetting.setRecommenders(loadRecommenders(confContent));
            } else {
                throw new RuntimeException("不正确的配置项：" + confKey);
            }
        }
    }

    private List<BlacklistRule> loadBlacklistRules(Object confContent){
        List<BlacklistRule> blacklistRules = new ArrayList<>();
        List<String> brSetting = (List<String>)confContent;
        for(String strBl : brSetting){
            BlacklistRule rule = BlacklistRuleType.getRuleByType(strBl);
            if(rule == null){
                throw new RuntimeException("不正确的黑名单规则：" + strBl);
            }
            blacklistRules.add(rule);
        }
        return blacklistRules;
    }
    
    private Map<String, RcmdAlgorithm> loadAlgorithms(Object confContent){
        Map<String, RcmdAlgorithm> result = new HashMap<>();
        List<String> algorithmSetting = (List<String>)confContent;
        for(String strAl : algorithmSetting){
            RcmdAlgorithm algorithm = algorithmFactory.getAlgorithmByName(strAl);
            if(algorithm == null){
                throw new RuntimeException("不正确的算法：" + strAl);
            }
            result.put(strAl, algorithm);
        }
        return result;
    }
    
    private Map<RcmdType, Recommender> loadRecommenders(Object confContent){
        Map<RcmdType, Recommender> recommenders = new HashMap<>();
        Map<String, Object> confObjs = (Map<String, Object>)confContent;
        for(String confKey : confObjs.keySet()){
            RcmdType rcmdType = RcmdType.geRcmdType(confKey);
            if(rcmdType == null){
                throw new RuntimeException("不正确的推荐类型：" + rcmdType);
            }
            Map<String, Object> recommenderSetting = (Map<String, Object>)confObjs.get(confKey);
            Recommender recommender = new Recommender();
            for(String settingKey : recommenderSetting.keySet()){
                Object settingContent = recommenderSetting.get(settingKey);
                if("cacheStrategy".equals(settingKey)){
                    CacheStrategy cacheStrategy = cacheStrategyFactory.getCacheStrategyByType((String)settingContent);
                    if(cacheStrategy == null){
                        throw new RuntimeException("不正确的缓存策略：" + settingContent);
                    }
                    recommender.setCacheStrategy(cacheStrategy);
                } else if("bizRules".equals(settingKey)){
                    recommender.setBizRules(loadBizRules(settingContent));
                } else if("defaultAlgorithm".equals(settingKey)){
                    RcmdAlgorithm defaultAlgorithm = algorithmFactory.getAlgorithmByName((String)settingContent);
                    if(defaultAlgorithm == null){
                        throw new RuntimeException("不正确的默认算法：" + settingContent);
                    }
                    recommender.setDefaultAlgorithm(defaultAlgorithm);
                } else if("shuntStrategy".equals(settingKey)){
                    recommender.setShuntStrategy(loadShuntStrategy(settingContent));
                } else {
                    throw new RuntimeException("不正确的推荐配置项：" + settingKey);
                }
            }
            
            recommender.validate();
            recommenders.put(rcmdType, recommender);
        }
        
        return recommenders;
    }
    
    private List<BizRule> loadBizRules(Object configContent){
        List<BizRule> result = new ArrayList<>();
        List<Map<String, Object>> rulesSetting = (List<Map<String, Object>>)configContent;
        for(Map<String, Object> ruleSetting : rulesSetting){
            String ruleType = (String)ruleSetting.get("ruleName");
            String initParam = (String)ruleSetting.get("initParam");
            BizRule rule = bizRuleFactory.getRuleByType(ruleType, initParam);
            if(rule == null){
                throw new RuntimeException("不正确的推荐规则：" + ruleSetting);
            }
            result.add(rule);
        }
        
        return result;
    }
    
    private ShuntStrategy loadShuntStrategy(Object configContent){
        Map<String, Object> confObjs = (Map<String, Object>)configContent;
        String strategyName = (String)confObjs.get("strategyName");
        String strategySetting = JsonHelper.fromJson(confObjs.get("setting"));
        return shuntStrategyFactory.getStrategyByName(strategyName, strategySetting);
    }
}
