package com.gupao.bd.eos.query.service.impl;

import com.gupao.bd.eos.query.support.ESClient;
import com.gupao.bd.eos.query.vo.ServiceLogSize;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.snapshots.SnapshotInfo;
import com.gupao.bd.eos.common.LogIndexBuilder;
import com.gupao.bd.eos.query.service.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * ES日志管理
 */
@Service
public class ESLogManager implements LogManager {
    private ESClient esClient;
    private LogIndexBuilder logIndexBuilder;

    private final String backupPath;
    private final String hdfsEndpoint;
    private final String HDFS_BACKUP_TYPE = "hdfs";
    private final String SNAPSHOT_NAME_PATTERN = "eos_log_snapshot_%s_%s";
    private final String REPO_NAME_PATTERN = "repo_%s";
    private final String DATE_PATTERN = "yyyyMMdd";

    private final ConcurrentHashMap featureResponses;

    @Autowired
    public ESLogManager(ESClient esClient,
                        LogIndexBuilder logIndexBuilder,
                        @Value("${backup.hdfs.endpoint}") String hdfsEndpoint,
                        @Value("${backup.path}") String backupPath) {
        this.esClient = esClient;
        this.logIndexBuilder = logIndexBuilder;
        this.hdfsEndpoint = hdfsEndpoint;
        this.backupPath = backupPath;
        this.featureResponses = new ConcurrentHashMap();
    }

    @Override
    public Map<String, Long> getServiceLogSize() throws IOException {
        Map<String, IndexStats> stats = getIndexStats();
        Map<String, Long> serviceLogSize = new HashMap<>();
        stats.forEach((index, indexStats) -> {
            String serviceId = logIndexBuilder.resolveServiceId(index);
            long size = indexStats.getTotal().getStore().getSizeInBytes();
            Long size_ = serviceLogSize.get(serviceId);
            if (null == size_) {
                serviceLogSize.put(serviceId, size);
            } else {
                serviceLogSize.put(serviceId, size + size_);
            }
        });

        return serviceLogSize;
    }

    @Override
    public List<ServiceLogSize> getServiceLogSizeOn(String startDate, String endDate) {
        Date startDate_ = new Date(0);
        Date endDate_ = new Date();
        if (Strings.hasText(startDate)) {
            startDate_ = validateDate(startDate);
        }
        if (Strings.hasText(endDate)) {
            endDate_ = validateDate(endDate);
        }

        Map<String, IndexStats> stats = getIndexStats();
        List<ServiceLogSize> serviceLogSizeList = new ArrayList<>();
        for (String index: stats.keySet()) {
            IndexStats indexStats = stats.get(index);
            String serviceId = logIndexBuilder.resolveServiceId(index);
            Date date = logIndexBuilder.resolveDate(index);

            if (0 <= date.compareTo(startDate_) && 0 >= date.compareTo(endDate_)) {
                long size = indexStats.getTotal().getStore().getSizeInBytes();
                serviceLogSizeList.add(new ServiceLogSize(date, serviceId, size));
            }
        };
        return serviceLogSizeList;
    }

    private Map<String, IndexStats> getIndexStats() {
        IndicesStatsResponse indicesStatsResponse = esClient.get()
                .admin()
                .indices()
                .prepareStats(logIndexBuilder.getIndexPattern())
                .all()
                .get();

        return indicesStatsResponse.getIndices();
    }

    private void ensureRepoExist(String repositoryName) {
        if (isRepositoryExist(repositoryName)) {
            return;
        }

        Settings settings = Settings.builder()
                .put("uri", hdfsEndpoint)
                .put("path", String.format("%s/%s", backupPath, repositoryName))
                .put("compress", true)
                .put("load_defaults", true)
                .build();

        esClient.get().admin().cluster().preparePutRepository(repositoryName)
                .setType(HDFS_BACKUP_TYPE).setSettings(settings).get();
    }

    private String getRepositoryName(String serviceId) {
        return String.format(REPO_NAME_PATTERN, serviceId);
    }

    private String getSnapshotName(String serviceId, String date) {
        return String.format(SNAPSHOT_NAME_PATTERN, serviceId, date);
    }

    private boolean isRepositoryExist(String repositoryName) {
        List<RepositoryMetaData> repositories =
                esClient.get().admin().cluster().prepareGetRepositories().get().repositories();

        if (repositories.size() > 0) {
            for (RepositoryMetaData repository : repositories) {
                if (repositoryName.equals(repository.name())) {
                    return true;
                }
            }
        }
        return false;

    }

    private boolean isSnapshotExist(String repositoryName, String snapshotName) {
        List<SnapshotInfo> snapshotInfo =
                esClient.get().admin().cluster().prepareGetSnapshots(repositoryName).get().getSnapshots();

        if (snapshotInfo.size() > 0) {
            for (SnapshotInfo snapshot : snapshotInfo) {
                if (snapshotName.equals(snapshot.snapshotId().getName())) {
                    return true;
                }
            }
        }

        return false;

    }

    @Override
    public String backup(String serviceId, String date) {
        Assert.hasText(serviceId);
        validateDate(date);

        // check index exits
        String index = logIndexBuilder.buildIndex(serviceId, date);
        if (!indexExists(index)) {
            throw new IllegalArgumentException("index of service: " + serviceId + " on: " + date + " does not exist");
        }

        // check snapshot exits
        String repositoryName = getRepositoryName(serviceId);
        String snapshot = getSnapshotName(serviceId, date);
        // create repo if not exist
        ensureRepoExist(repositoryName);
        if (isSnapshotExist(repositoryName, snapshot)) {
            return null;
        }

        // create snapshot
        CreateSnapshotRequest request = new CreateSnapshotRequest(repositoryName, snapshot).indices(index);
        ActionFuture<CreateSnapshotResponse> resp = esClient.get().admin().cluster().createSnapshot(request);
        return newRequestId(resp);
    }

    @Override
    public String getBackupStatus(String requestId) {
        Assert.hasText(requestId);
        Object object = featureResponses.get(requestId);
        if (null == object) {
            throw new IllegalArgumentException("bad requestId");
        }
        if (!(object instanceof ActionFuture)) {
            throw new IllegalArgumentException("bad requestId");
        }
        ActionFuture<CreateSnapshotResponse> resp = (ActionFuture<CreateSnapshotResponse>) object;
        return resp.actionGet().status().name();
    }

    private boolean indexExists(String index) {
        try {
            ActionFuture<IndicesExistsResponse> resp =
                    esClient.get().admin().indices().exists(new IndicesExistsRequest(index));
            return resp.get().isExists();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Date validateDate(String date) {
        Assert.hasText(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid date, correct format: " + DATE_PATTERN);
        }
    }

    @Override
    public String restore(String serviceId, String date) {
        Assert.hasText(serviceId);
        validateDate(date);

        String repositoryName = getRepositoryName(serviceId);
        String snapshotName = getSnapshotName(serviceId, date);

        if (!isRepositoryExist(repositoryName)) {
            throw new IllegalArgumentException("repository: " + repositoryName + " does not exist, snapshot does not create?");
        }

        if (!isSnapshotExist(repositoryName, snapshotName)) {
            throw new IllegalArgumentException("snapshot: " + snapshotName + " does not exist?");
        }

        RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest(repositoryName, snapshotName);
        ActionFuture<RestoreSnapshotResponse> resp = esClient.get().admin().cluster().restoreSnapshot(restoreSnapshotRequest);

        return newRequestId(resp);
    }

    @Override
    public String getRestoreStatus(String requestId) {
        Assert.hasText(requestId);
        Object object = featureResponses.get(requestId);
        if (null == object) {
            throw new IllegalArgumentException("bad requestId");
        }
        if (!(object instanceof ActionFuture)) {
            throw new IllegalArgumentException("bad requestId");
        }
        ActionFuture<RestoreSnapshotResponse> resp = (ActionFuture<RestoreSnapshotResponse>) object;
        return resp.actionGet().status().name();
    }

    @Override
    public List<String> getServiceIdList() {
        ImmutableOpenMap<String, IndexMetaData> indexesMap = esClient.get().admin().cluster()
                .prepareState().execute()
                .actionGet().getState()
                .getMetaData().getIndices();

        List<String> indexes = new ArrayList<>();
        indexesMap.keysIt().forEachRemaining(indexes::add);
        List<String> serviceIds = new ArrayList<>();
        indexes.forEach(index -> {
            if (logIndexBuilder.validateIndex(index)) {
                String serviceId = logIndexBuilder.resolveServiceId(index);
                if (!serviceIds.contains(serviceId)) {
                    serviceIds.add(serviceId);
                }
            }
        });

        return serviceIds;
    }

    private <E> String newRequestId(ActionFuture<E> resp) {
        String requestId = UUID.randomUUID().toString();
        featureResponses.put(requestId, resp);
        return requestId;
    }
}
