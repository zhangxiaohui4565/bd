package com.gupao.bd.eos.collector;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gupao.bd.eos.common.LogIndexBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 日志ES存储实现
 */
public class LogEsVisitor extends LogVisitor {
    private final static Logger logger = LoggerFactory.getLogger(LogEsVisitor.class);

    private final String DEFAULT_INDEX_TYPE = "all";

    private String indexType = DEFAULT_INDEX_TYPE;
    private LogIndexBuilder logIndexBuilder;

    private String clusterName;
    private List<String> esTransportAddresses;
    private TransportClient esClient;
    private BulkProcessor bulkProcessor;

    private boolean bulkSuccess;
    private int retryTimes = 10;

    private int batchSize = 4096;

    private final long NO_CURRENT_THREAD = -1L;
    /**
     * 记住当前线程id，防止多线程访问
     */
    private final AtomicLong currentThread = new AtomicLong(NO_CURRENT_THREAD);

    /**
     * 引用计数，允许当前线程重入
     */
    private final AtomicInteger refcount = new AtomicInteger(0);

    /**
     * 构造器
     * 包含es连接必要的参数
     * @param clusterName
     * @param esTransportAddresses
     */
    public LogEsVisitor(String clusterName, String[] esTransportAddresses) {
        this.clusterName = clusterName;
        this.esTransportAddresses = Arrays.asList(esTransportAddresses);
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * 初始化
     * 初始化客户端
     * 批量处理客户端
     */
    public void init() {
        Settings settings = Settings.builder()
                .put("cluster.name", this.clusterName)
                .build();
        esClient = new PreBuiltTransportClient(settings);
        esTransportAddresses.forEach(esNode -> {
            String[] node = esNode.split(":");
            try {
                esClient.addTransportAddress(new TransportAddress(InetAddress.getByName(node[0]),
                        Integer.parseInt(node[1])));
            } catch (UnknownHostException e) {
                throw new RuntimeException("Bad es node config", e);
            }
        });

        // 日志索引util
        logIndexBuilder = new LogIndexBuilder();

        // 日志批量处理客户端
        bulkProcessor = BulkProcessor.builder(esClient,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("beforeBulk: executionId: {}", executionId);
                        }
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("afterBulk: executionId: {}", executionId);
                        }
                        bulkSuccess = true;
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                        logger.error("Failed to execute bulk process, executionId: " + executionId, failure);
                        bulkSuccess = false;
                    }
                })
                // 设置批处理大小
                .setBulkActions(this.batchSize)
                // 设置并发数，0表示同步执行
                .setConcurrentRequests(0)
                // 重试策略：指数级后退，每次300ms，重试10次
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(300), retryTimes))
                .build();
    }

    /**
     * 处理日志
     * 验证必要参数
     * 转换为es支持格式
     */
    @Override
    public boolean accept(Log log) {
        acquire();
        try {
            if (null == log.getUuid() || null == log.getTimestamp() || null == log.getServiceId()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Bad log: {}, missing uuid, timestamp or serviceId", log);
                }
                return false;
            }
            bulkProcessor.add(new IndexRequest(logIndexBuilder.buildIndex(log.getServiceId(), log.getTimestamp()),
                    indexType, log.getUuid())
                    .source(log.toJSON(), XContentType.JSON));
            return true;
        } finally {
            release();
        }
    }

    /**
     * 获取乐观锁
     * 防止多个线程同时访问
     */
    private void acquire() {
        long threadId = Thread.currentThread().getId();
        if (threadId != currentThread.get() && !currentThread.compareAndSet(NO_CURRENT_THREAD, threadId)) {
            throw new ConcurrentModificationException("KafkaConsumer is not safe for multi-threaded access");
        }
        refcount.incrementAndGet();
    }

    /**
     * 释放乐观锁
     */
    private void release() {
        if (refcount.decrementAndGet() == 0) {
            currentThread.set(NO_CURRENT_THREAD);
        }
    }

    /**
     * 刷新数据到Es
     * @return
     */
    @Override
    public boolean flush() {
        acquire();
        try {
            bulkProcessor.flush();
            return bulkSuccess;
        } finally {
            release();
        }
    }

    /**
     * 消耗对象
     * 断开连接，清理资源
     */
    public void destroy() {
        if (null != bulkProcessor) {
            try {
                bulkProcessor.awaitClose(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("bulkProcessor.awaitClose interrupted");
                Thread.currentThread().interrupt();
            } finally {
                if (null != esClient) {
                    esClient.close();
                }
            }
        }
    }
}
