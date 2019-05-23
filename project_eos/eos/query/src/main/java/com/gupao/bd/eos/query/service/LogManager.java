package com.gupao.bd.eos.query.service;

import com.gupao.bd.eos.query.vo.ServiceLogSize;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 日志管理接口
 */
public interface LogManager {
    /**
     * 获取日志大小
     * @return
     * @throws IOException
     */
    Map<String, Long> getServiceLogSize() throws IOException;

    /**
     * 获取日志在某个时间段的大写
     * @param startDate
     * @param endDate
     * @return
     */
    List<ServiceLogSize> getServiceLogSizeOn(String startDate, String endDate);

    /**
     * 备份日志
     * @param serviceId
     * @param date
     * @return
     */
    String backup(String serviceId, String date);

    /**
     * 获取备份状态
     * @param requestId
     * @return
     */
    String getBackupStatus(String requestId);

    /**
     * 恢复日志
     * @param serviceId
     * @param date
     * @return
     */
    String restore(String serviceId, String date);

    /**
     * 获取恢复状态
     * @param requestId
     * @return
     */
    String getRestoreStatus(String requestId);

    /**
     * 获取服务列表
     * @return
     */
    List<String> getServiceIdList();
}
