/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.search;


import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.gupao.bd.sample.rcmdsys.controller.MovieVO;
import com.gupao.bd.sample.rcmdsys.search.SearchCriteria.Operator;

/**
 * 基于ES的电影查询实现
 * @author george 
 */
public class ESMovieSearcher implements MovieSearcher {

    private TransportClient client;
    private String movieIndexName;
    private String movieIndexType;
    
    public ESMovieSearcher(String esClusterName, String movieIndexName, String movieIndexType, 
            String esClusterHosts){
        
        Settings settings = Settings.builder().put("cluster.name", esClusterName).build();
        this.client = new PreBuiltTransportClient(settings);
        if (StringUtils.isEmpty(esClusterHosts)) {
            throw new RuntimeException("沒有指定ES节点的地址，请以如下格式指定：host1:port1;host2:port2");
        }
        String[] hosts = esClusterHosts.split(";");
        for (String host : hosts) {
            String[] hostAndPort = host.split(":");
            this.client.addTransportAddress(
                    new TransportAddress(new InetSocketAddress(hostAndPort[0], Integer.valueOf(hostAndPort[1]))));
        }
        
        this.movieIndexName = movieIndexName;
        this.movieIndexType = movieIndexType;
    }

    @Override
    public List<MovieVO> searchMovies(MovieQuery query) {
        
        List<MovieVO> result = new ArrayList<>();
        try {
            QueryBuilder queryBuilder = generateESQuery(query);
            SearchResponse response = this.client.prepareSearch(this.movieIndexName).setTypes(this.movieIndexType)
                    .setQuery(queryBuilder)
                    .setFrom(query.getStart())
                    .setSize(query.getFetchSize()).get();
            for(SearchHit hit : response.getHits()){
                Map<String, Object> fields = hit.getSourceAsMap();
                result.add(new MovieVO(hit.getId(), (String)fields.get("imdb_id"), (String)fields.get("name"), 
                        (List<String>)fields.get("tags"), (String)fields.get("image_url"), (Integer)fields.get("year")));
            }
        } catch (Exception e) {
            throw new RuntimeException("查询电影异常", e);
        }
        return result;
    }

    private QueryBuilder generateESQuery(MovieQuery query) throws Exception {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        
        // 构造过滤条件
        if (query.getFilterCriterias() != null) {
            for (SearchCriteria criteria : query.getFilterCriterias()) {
                queryBuilder = queryBuilder.filter(constructQB(criteria));
            }
        }

        // 构造查询条件
        if (query.getSearchCriterias() != null) {
            for (SearchCriteria criteria : query.getSearchCriterias()) {
                queryBuilder = queryBuilder.must(constructQB(criteria));
            }
        }
        
        return queryBuilder;
    }

    private QueryBuilder constructQB(SearchCriteria criteria) {
        if (MovieField.ID.equals(criteria.getField())) {
            return QueryBuilders.idsQuery(this.movieIndexType).addIds(criteria.getValue().split(","));
        } else {
            if (Operator.IN.equals(criteria.getOperator())) {
                return QueryBuilders.termsQuery(criteria.getField().getEsFieldName(), criteria.getValue());
            } else if (Operator.EQUAL.equals(criteria.getOperator())) {
                return QueryBuilders.termQuery(criteria.getField().getEsFieldName(), criteria.getValue());
            } else if (Operator.NOT_EQUAL.equals(criteria.getOperator())) {
                return QueryBuilders.boolQuery()
                        .mustNot(QueryBuilders.termQuery(criteria.getField().getEsFieldName(), criteria.getValue()));
            } else if (Operator.CONTAIN.equals(criteria.getOperator())) {
                return QueryBuilders.matchQuery(criteria.getField().getEsFieldName(), criteria.getValue());
            }
        }
        throw new RuntimeException("不支持的查询条件：" + criteria.toString());
    }
    
}