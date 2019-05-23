package com.gupao.bd.eos.query.service.impl;

import com.alibaba.fastjson.JSON;
import com.gupao.bd.eos.query.service.LogQuery;
import com.gupao.bd.eos.query.support.ESClient;
import com.gupao.bd.eos.query.vo.*;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import com.gupao.bd.eos.common.LogIndexBuilder;

import java.util.*;

/**
 * Elasticsearch 日志查询实现
 */
@Service
public class ESLogQuery implements LogQuery {
    private ESClient esClient;
    private LogIndexBuilder logIndexBuilder;

    private final static String ALL_LOG_LEVEL = "ALL";

    @Autowired
    public ESLogQuery(ESClient esClient, LogIndexBuilder logIndexBuilder) {
        this.esClient = esClient;
        this.logIndexBuilder = logIndexBuilder;
    }

    @Override
    public QueryResult query(QueryRequest queryRequest) {
        // 参数验证
        Assert.notNull(queryRequest);
        queryRequest.validate();

        // 数据过滤filter
        // 时间是必须的
        BoolQueryBuilder filterQuery = QueryBuilders.boolQuery().must(QueryBuilders
                .rangeQuery(LogFields.TIMESTAMP)
                .from(queryRequest.getStartTime()).to(queryRequest.getEndTime()));

        // 过滤level
        if (StringUtils.hasText(queryRequest.getLevel())
                && !ALL_LOG_LEVEL.equals(queryRequest.getLevel())) {
            filterQuery.must(
                    QueryBuilders.termQuery(LogFields.LEVEL, queryRequest.getLevel()));
        }
        // 过滤ip
        if (StringUtils.hasText(queryRequest.getIp())) {
            filterQuery.must(
                    QueryBuilders.termQuery(LogFields.IP, queryRequest.getIp()));
        }

        // 按规则获取es索引
        List<String> indexes = logIndexBuilder.buildIndexes(
                queryRequest.getServiceId(),
                queryRequest.getStartTime(),
                queryRequest.getEndTime());

        // 过滤不存在是索引
        String[] indexesArray = indexes.stream().filter(index -> indexExists(index)).toArray(String[]::new);

        // 索引不存在则返回空
        if (0 == indexesArray.length) {
            return new QueryResult();
        }

        int fromOffset = queryRequest.getPage() * queryRequest.getPageSize();
        SearchRequestBuilder searchRequest = esClient.get().prepareSearch(indexesArray)
                .setTypes(esClient.getIndexType())
                .setFrom(fromOffset).setSize(queryRequest.getPageSize())
                .addSort(LogFields.TIMESTAMP, SortOrder.DESC);

        // 查询字符串，搜索框输入支持lucene最底层语法
        // 设置查询和过滤器
        QueryStringQueryBuilder queryStringQuery = null;
        if (StringUtils.hasText(queryRequest.getQueryString())) {
            queryStringQuery = QueryBuilders.queryStringQuery(queryRequest.getQueryString())
                    .field(LogFields.MESSAGE)
                    .field(LogFields.EXCEPTION_MESSAGE);
            searchRequest.setQuery(QueryBuilders.boolQuery().must(queryStringQuery).filter(filterQuery));
        } else {
            searchRequest.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).filter(filterQuery));
        }

        // 设置高亮字段
        if (null != queryStringQuery) {
            HighlightBuilder mhBuilder = new HighlightBuilder();
            mhBuilder.preTags("<em>");
            mhBuilder.postTags("</em>");
            mhBuilder.field(LogFields.MESSAGE);
            mhBuilder.field(LogFields.EXCEPTION_MESSAGE);
            mhBuilder.highlightQuery(queryStringQuery);
            searchRequest.highlighter(mhBuilder);
        }

        // 查询es
        SearchResponse response = searchRequest.get();

        // 是否有记录？
        SearchHits searchHits = response.getHits();
        if (null == searchHits) {
            return QueryResult.emptyResult();
        }

        // 设置统计
        QueryResult queryResult = new QueryResult();
        queryResult.setTook(response.getTook().getMillis());
        queryResult.setTotalCount(searchHits.getTotalHits());

        // 设置返回数据
        SearchHit[] hits = searchHits.getHits();
        List<Log> logs = new ArrayList<>(hits.length);
        for (int i = 0; i < hits.length; i++) {
            String logJson = hits[i].getSourceAsString();
            Log log = JSON.parseObject(logJson, Log.class);
            logs.add(log);

            // 高亮字段存在？
            Map<String, HighlightField> highlightFields = hits[i].getHighlightFields();
            if (null == highlightFields) {
                continue;
            }

            // 设置高亮字段
            log.setMsgHighlight(getHighlight(highlightFields, LogFields.MESSAGE));
            log.setExMsgHighlight(getHighlight(highlightFields, LogFields.EXCEPTION_MESSAGE));
        }
        queryResult.setLogs(logs);

        return queryResult;
    }

    @Override
    public List<Sums> sums(StatsRequest statsRequest) {
        // 参数验证
        Assert.notNull(statsRequest);
        statsRequest.validate();

        // 数据过滤filter
        // 时间是必须的
        BoolQueryBuilder filterQuery = QueryBuilders.boolQuery().must(QueryBuilders
                .rangeQuery(LogFields.TIMESTAMP)
                .from(statsRequest.getStartTime()).to(statsRequest.getEndTime()));

        String firstFiled = statsRequest.getFirstGroupField();
        TermsAggregationBuilder termAggrBuilder = AggregationBuilders
                .terms(firstFiled)
                .field(firstFiled)
                // 设置返回所有记录
                .size(Integer.MAX_VALUE);

        // 二层聚合
        String secondGroupField =  statsRequest.getSecondGroupField();
        if (StringUtils.hasText(secondGroupField)) {
            termAggrBuilder.subAggregation(
                    AggregationBuilders
                            .terms(secondGroupField)
                            .field(secondGroupField));
        }

        // 查询es
        SearchResponse response = esClient.get().prepareSearch(logIndexBuilder.getIndexPattern())
                .setTypes(esClient.getIndexType())
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery()).filter(filterQuery))
                .addAggregation(termAggrBuilder)
                .get();

        // 解析结果
        Map<String, Aggregation> aggMap = response.getAggregations().asMap();
        StringTerms stringTerms = (StringTerms) aggMap.get(firstFiled);

        // 聚合结果为空
        if (stringTerms.getBuckets().isEmpty()) {
            return Collections.emptyList();
        }

        // 解析聚合结果
        List<Sums> sumsList = new ArrayList<>();
        stringTerms.getBuckets().forEach(firstBucket -> {
            Sums sums = new Sums(firstBucket.getKeyAsString(), firstBucket.getDocCount());
            sumsList.add(sums);

            if (StringUtils.hasText(secondGroupField)) {
                StringTerms secondTerms = (StringTerms) firstBucket.getAggregations().asMap().get(secondGroupField);
                if (!secondTerms.getBuckets().isEmpty()) {
                    secondTerms.getBuckets().forEach(secondBucket -> {
                        sums.setSubSum(new Sums(secondBucket.getKeyAsString(), secondBucket.getDocCount()));
                    });
                }
            }
        });

        return sumsList;
    }

    /**
     * 设置高亮字段
     * @param highlightFields
     * @param field
     * @return
     */
    private String getHighlight(Map<String, HighlightField> highlightFields, String field) {
        StringBuilder highlights = new StringBuilder();
        HighlightField highlightField = highlightFields.get(field);
        if (null == highlightField) {
            return null;
        }
        Text[] texts = highlightField.getFragments();
        Arrays.stream(texts).forEach(text -> {
            highlights.append(text.string());
        });
        return highlights.toString();
    }

    /**
     * 验证索引是否在es存在
     *
     * @param index
     * @return
     */
    public boolean indexExists(String index) {
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = esClient.get().admin().indices().exists(request).actionGet();
        return response.isExists();
    }
}
