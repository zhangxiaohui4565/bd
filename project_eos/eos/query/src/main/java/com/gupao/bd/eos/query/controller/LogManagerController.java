package com.gupao.bd.eos.query.controller;

import com.gupao.bd.eos.query.vo.Response;
import com.gupao.bd.eos.query.service.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 日志管理接口
 */
@RestController
public class LogManagerController {
    private LogManager logManager;

    @Autowired
    public LogManagerController(LogManager logManager) {
        this.logManager = logManager;
    }

    /**
     * 列出所有的服务id
     * @return
     */
    @RequestMapping(value = "/all_service_ids", method = RequestMethod.GET)
    public Response getServiceIdList() {
        return new Response(logManager.getServiceIdList());
    }

    /**
     * 按服务列出日志大小
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/all_service_log_size", method = RequestMethod.GET)
    public Response getServiceLogSize() throws IOException {
        return new Response(logManager.getServiceLogSize());
    }

    /**
     * 按日期、服务列出日志大小
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/service_log_size", method = RequestMethod.GET)
    public Response getServiceLogSizeOn(@RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate) {
        return new Response(logManager.getServiceLogSizeOn(startDate, endDate));
    }

    /**
     * 备份某个服务日志
     * @param serviceId
     * @param date
     * @return
     */
    @RequestMapping(value = "/backup_service_logs", method = RequestMethod.POST)
    public Response backup(String serviceId, String date) {
        return new Response(logManager.backup(serviceId, date));
    }

    /**
     * 查询备份进度
     * @param requestId
     * @return
     */
    @RequestMapping(value = "/backup_status", method = RequestMethod.GET)
    public Response getBackupStatus(String requestId) {
        return new Response(logManager.getBackupStatus(requestId));
    }

    /**
     * 恢复某个服务日志
     * @param serviceId
     * @param date
     * @return
     */
    @RequestMapping(value = "/restore_service_logs", method = RequestMethod.POST)
    public Response restore(String serviceId, String date) {
        return new Response(logManager.restore(serviceId, date));
    }

    /**
     * 查询恢复进度
     * @param requestId
     * @return
     */
    @RequestMapping(value = "/restore_status", method = RequestMethod.GET)
    public Response getRestoreStatus(String requestId) {
        return new Response(logManager.getRestoreStatus(requestId));
    }
}
