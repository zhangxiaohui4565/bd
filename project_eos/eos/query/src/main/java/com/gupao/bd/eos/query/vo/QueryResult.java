package com.gupao.bd.eos.query.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * 查询结果
 */
@Getter
@Setter
public class QueryResult {
    private long totalCount;
    private long took;

    List<Log> logs;

    public static QueryResult emptyResult() {
        QueryResult queryResult = new QueryResult();
        queryResult.setLogs(Collections.emptyList());
        return queryResult;
    }
}
