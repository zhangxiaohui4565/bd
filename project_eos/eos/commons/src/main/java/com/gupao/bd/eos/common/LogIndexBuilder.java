package com.gupao.bd.eos.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日志索引生成辅助类
 */
public class LogIndexBuilder {
    private final String DEFAULT_INDEX_PREFIX = "eos_service_log";
    private String indexPrefix = DEFAULT_INDEX_PREFIX;
    private final static String DATE_PATTERN = "yyyyMMdd";

    private final static long MILLS_OF_DAY = 86400 * 1000;

    public LogIndexBuilder() {
        super();
    }

    public LogIndexBuilder(String indexPrefix) {
        this.indexPrefix = indexPrefix;
    }

    public String buildIndex(String serviceId, long logTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        String dateStr = simpleDateFormat.format(new Date(logTime));
        return String.format("%s_%s_%s", indexPrefix, serviceId, dateStr);
    }

    public String buildIndex(String serviceId, String date) {
        return String.format("%s_%s_%s", indexPrefix, serviceId, date);
    }

    public String getIndexPrefix() {
        return indexPrefix;
    }

    public String getIndexPattern() {
        return String.format("%s_*", indexPrefix);
    }

    public boolean validateIndex(String index) {
        //TODO: more precisely
        return index.startsWith(indexPrefix);
    }

    public String getServiceIndexPattern(String serviceId) {
        return String.format("%s_%s_*", indexPrefix, serviceId);
    }


    public List<String> buildIndexes(String serviceId, long startTime, long endTime) {
        if (endTime - startTime > MILLS_OF_DAY * 3) {
            throw new IllegalArgumentException("endTime cannot exceed startTime by three days");
        }

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(new Date(startTime));
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(new Date(endTime));
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        List<String> indexes = new ArrayList<>();
        while (0 >= startCal.compareTo(endCal)) {
            indexes.add(buildIndex(serviceId, startCal.getTime().getTime()));
            startCal.add(Calendar.DATE, 1);
        }
        return indexes;
    }

    public String resolveServiceId(String index) {
        if (!index.startsWith(indexPrefix)) {
            throw new IllegalArgumentException("invalid index: " + index);
        }
        // service_log_serviceId_20180811
        int start = indexPrefix.length() + 1;
        int end = index.lastIndexOf('_');
        return index.substring(start, end);
    }

    public Date resolveDate(String index) {
        if (!index.startsWith(indexPrefix)) {
            throw new IllegalArgumentException("invalid index: " + index);
        }
        // service_log_serviceId_20180811
        int start = index.lastIndexOf('_');
        String sdate = index.substring(start + 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        try {
            return simpleDateFormat.parse(sdate);
        } catch (ParseException e) {
            throw new RuntimeException("invalid date in index: " + index);
        }
    }
}
