package com.gupao.bd.eos.query.controller;

import com.gupao.bd.eos.query.service.LogQuery;
import com.gupao.bd.eos.query.vo.LogFields;
import com.gupao.bd.eos.query.vo.QueryRequest;
import com.gupao.bd.eos.query.vo.Response;
import com.gupao.bd.eos.query.vo.StatsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志查询统计接口
 */
@RestController
public class LogQueryController {
    private LogQuery logQuery;

    @Autowired
    public LogQueryController(LogQuery logQuery) {
        this.logQuery = logQuery;
    }

    /**
     * 日志查询接口
     * @param queryRequest
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Response query(@RequestBody QueryRequest queryRequest) {
        return new Response(logQuery.query(queryRequest));
    }

    /**
     * 通用统计接口
     * @param statsRequest
     * @return
     */
    @RequestMapping(value = "/sums", method = RequestMethod.POST)
    public Response sums(@RequestBody StatsRequest statsRequest) {
        return new Response(logQuery.sums(statsRequest));
    }

    /**
     * ip统计接口
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/ip_sums", method = RequestMethod.GET)
    public Response getIpSums(long startTime, long endTime) {
        StatsRequest statsRequest = new StatsRequest();
        statsRequest.setStartTime(startTime);
        statsRequest.setEndTime(endTime);
        statsRequest.setFirstGroupField(LogFields.IP);
        return new Response(logQuery.sums(statsRequest));
    }

    /**
     * 实例统计接口
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/service_instance_sums", method = RequestMethod.GET)
    public Response getServiceInstanceSums(long startTime, long endTime) {
        StatsRequest statsRequest = new StatsRequest();
        statsRequest.setStartTime(startTime);
        statsRequest.setEndTime(endTime);
        statsRequest.setFirstGroupField(LogFields.SERVICE_ID);
        statsRequest.setSecondGroupField(LogFields.INSTANCE_ID);
        return new Response(logQuery.sums(statsRequest));
    }
}
