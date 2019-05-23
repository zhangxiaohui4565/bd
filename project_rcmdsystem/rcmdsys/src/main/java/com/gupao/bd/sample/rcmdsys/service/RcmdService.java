package com.gupao.bd.sample.rcmdsys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gupao.bd.sample.rcmdsys.algorithm.MoviePO;
import com.gupao.bd.sample.rcmdsys.algorithm.RcmdAlgorithm;
import com.gupao.bd.sample.rcmdsys.algorithm.RcmdAlgorithmFactory;
import com.gupao.bd.sample.rcmdsys.bizrule.BizRule;
import com.gupao.bd.sample.rcmdsys.blacklist.BlacklistRule;
import com.gupao.bd.sample.rcmdsys.cache.CacheStrategy;
import com.gupao.bd.sample.rcmdsys.conf.RcmdSetting;
import com.gupao.bd.sample.rcmdsys.conf.RcmdSettingLoader;
import com.gupao.bd.sample.rcmdsys.conf.Recommender;
import com.gupao.bd.sample.rcmdsys.controller.MovieVO;
import com.gupao.bd.sample.rcmdsys.controller.RcmdContext;
import com.gupao.bd.sample.rcmdsys.search.MovieField;
import com.gupao.bd.sample.rcmdsys.search.MovieQuery;
import com.gupao.bd.sample.rcmdsys.search.MovieSearcher;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria.Operator;
import com.gupao.bd.sample.rcmdsys.shunt.ShuntStrategy;

/**
 * @author george 
 * 推荐服务主实现
 */
@Component
public class RcmdService {

    private final static Log LOGGER = LogFactory.getLog(RcmdService.class);

    @Autowired
    private RcmdSettingLoader rcmdSettingLoader;
    @Autowired
    private MovieSearcher movieSearcher;
    @Autowired
    private RcmdResultLogger resultLogger;
    @Autowired
    private RcmdAlgorithmFactory algorithmFactory;

    public RcmdResult recommend(RcmdContext context) {
        
        RcmdResult result = new RcmdResult();
        result.setRcmdId(UUID.randomUUID().toString());
        
        // 根据输入参数找到对应场景的【推荐配置】
        RcmdSetting rcmdSetting = rcmdSettingLoader.getRcmdSetting();
        Recommender recommender = rcmdSetting.getRecommender(context.getRcmdType());

        // 判断是否从缓存中获取
        CacheStrategy cacheStrategy = recommender.getCacheStrategy();
        if (cacheStrategy != null) {
            RcmdResult cacheResult = cacheStrategy.get(context);
            if (cacheResult != null) {
                return cacheResult;
            }
        }

        // 从配置中获取【分流策略】，根据分流策略获取【推荐算法】
        RcmdAlgorithm algorithm = recommender.getDefaultAlgorithm();
        ShuntStrategy shuntStrategy = recommender.getShuntStrategy();
        if (shuntStrategy != null) {
            String algorithmName = shuntStrategy.getRcmdAlgorithm(context);
            if (algorithmName == null) {
                LOGGER.warn("从分流策略取出的算法为空，需检查设置或参数是否正确，将使用默认的算法推荐。");
            } else {
                algorithm = algorithmFactory.getAlgorithmByName(algorithmName);
            }
            // 设置算法名称以用于效果收集
            result.setRcmdAlgorithm(algorithmName);
        }

        // 调用推荐算法，获取【推荐结果】
        List<MoviePO> algorithmResult = algorithm.recommend(context);

        // 将推荐结果和【黑名单】传递给【电影搜索器】，搜索电影，返回最终结果
        MovieQuery query = constructMovieQuery(algorithmResult, rcmdSetting.getBlacklistRules(), context);
        query.setFetchSize(context.getItemCount());
        query.setStart(0);
        List<MovieVO> rcmdItems = movieSearcher.searchMovies(query);

        // 应用业务规则
        if (recommender.getBizRules() != null) {
            for (BizRule bizRule : recommender.getBizRules()) {
                rcmdItems = bizRule.applyRule(rcmdItems);
            }
        }
        
        // 对结果截取
        if(rcmdItems.size() > context.getItemCount()) {
            rcmdItems = rcmdItems.subList(0, context.getItemCount());
        }
        result.setRcmdItems(rcmdItems);

        // 缓存结果
        if (cacheStrategy != null) {
            cacheStrategy.put(result, context);
        }

        // 记录推荐日志
        resultLogger.logRcmdResult(rcmdItems, context);

        return result;
    }

    private MovieQuery constructMovieQuery(List<MoviePO> algorithmResult, List<BlacklistRule> blacklistRules,
            RcmdContext context) {
        MovieQuery query = new MovieQuery();
        List<SearchCriteria> filterCriterias = new ArrayList<>();
        List<Long> movieIds = new ArrayList<>();
        for(MoviePO moviePo : algorithmResult){
            movieIds.add(moviePo.getMovieId());
        }
        if(!movieIds.isEmpty()) {
            filterCriterias.add(new SearchCriteria(MovieField.ID, StringUtils.join(movieIds, ","), Operator.IN));
        }
        
        if(blacklistRules != null){
            for(BlacklistRule rule : blacklistRules){
                filterCriterias.addAll(rule.getFilterConditions());
            }
        }
        
        query.setFilterCriterias(filterCriterias);
        return query;
    }
}
