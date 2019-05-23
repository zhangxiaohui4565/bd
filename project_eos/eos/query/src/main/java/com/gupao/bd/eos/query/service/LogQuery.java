package com.gupao.bd.eos.query.service;

import com.gupao.bd.eos.query.vo.QueryRequest;
import com.gupao.bd.eos.query.vo.QueryResult;
import com.gupao.bd.eos.query.vo.StatsRequest;
import com.gupao.bd.eos.query.vo.Sums;

import java.util.List;

/**
 * 日志查询接口
 */
public interface LogQuery {
    /**
     * 查询日志
     * @param queryRequest
     * @return
     */
    QueryResult query(QueryRequest queryRequest);

    /**
     * 聚合日志
     * @param statsRequest
     * @return
     */
    List<Sums> sums(StatsRequest statsRequest);
}
