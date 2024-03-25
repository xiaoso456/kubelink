package io.github.xiaoso456.kubelink.service;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.UUID;
import io.github.xiaoso456.kubelink.api.Copy;
import io.github.xiaoso456.kubelink.domain.SyncInfo;
import io.github.xiaoso456.kubelink.enums.SyncType;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SyncManagementService {

    @Autowired
    ConfigManagementService configManagementService;

    private Map<String, SyncInfo> idSyncInfoMap = new ConcurrentHashMap<>();
    private Map<String, WatchMonitor> idWatchMonitor = new ConcurrentHashMap<>();

    public void createOrUpdateSyncInfo(SyncInfo syncInfo) {
        removeSyncInfo(syncInfo.getId());
        idSyncInfoMap.put(syncInfo.getId(), syncInfo);
        if(syncInfo.getAutoSync() && syncInfo.getEnable() && syncInfo.getSyncType() == SyncType.LOCAL_TO_POD){
            File watchFile = new File(syncInfo.getSource());
            WatchMonitor watchMonitor = WatchMonitor.create(watchFile, WatchMonitor.EVENTS_ALL);
            watchMonitor.setWatcher(new Watcher() {
                @Override
                public void onCreate(WatchEvent<?> event, Path currentPath) {
                    log.info("sync id [{}], file create, start to sync.",syncInfo.getId());
                    sync(syncInfo);
                }

                @Override
                public void onModify(WatchEvent<?> event, Path currentPath) {
                    log.info("sync id [{}], file modify, start to sync.",syncInfo.getId());
                    sync(syncInfo);
                }

                @Override
                public void onDelete(WatchEvent<?> event, Path currentPath) {
                    // todo remove or do nothing?
                }

                @Override
                public void onOverflow(WatchEvent<?> event, Path currentPath) {
                    log.error("watch id [{}] failed, please restart",syncInfo.getId());
                }
            });
            watchMonitor.start();
            idWatchMonitor.put(syncInfo.getId(),watchMonitor);
        }
    }

    public boolean removeSyncInfo(String id) {
        SyncInfo remove = idSyncInfoMap.remove(id);
        WatchMonitor watchMonitor = idWatchMonitor.remove(id);
        if(watchMonitor != null){
            watchMonitor.close();
        }
        return remove != null;
    }

    public List<SyncInfo> listSyncInfo(){
        return new ArrayList<>(idSyncInfoMap.values());
    }

    public SyncInfo getSyncInfo(String id){
        return idSyncInfoMap.get(id);
    }

    public void sync(SyncInfo syncInfo) {
        ApiClient apiClient = configManagementService.getApiClient();
        Copy copy = new Copy();
        copy.setApiClient(apiClient);
        StopWatch stopWatch = StopWatch.create(UUID.fastUUID().toString());
        stopWatch.start();
        if (syncInfo.getSyncType() == SyncType.LOCAL_TO_POD && syncInfo.getEnable()) {
            try {
                copy.copyFileToPod(syncInfo.getNamespace(), syncInfo.getPod(), syncInfo.getContainer(), Paths.get(syncInfo.getSource()), Paths.get(syncInfo.getTarget()));
                stopWatch.stop();
                log.info("Sync id [{}] task success,cost [{}] ms", syncInfo.getId(), stopWatch.getTotalTimeMillis());
            } catch (ApiException | IOException e) {
                log.error("Sync id [{}] task failed", syncInfo.getId());
                throw new RuntimeException(e);
            }
            return;
        }

        if(syncInfo.getSyncType() == SyncType.POD_TO_LOCAL && syncInfo.getEnable()){
            if (syncInfo.getAutoSync()){
                log.warn("Unsupported auto sync from pod to local, sync id [{}]",syncInfo.getId());
            }
            try {
                copy.copyFileFromPod(syncInfo.getNamespace(),syncInfo.getPod(),syncInfo.getContainer(), syncInfo.getSource(), Paths.get(syncInfo.getTarget()));
                stopWatch.stop();
                log.info("Sync id [{}] task success,cost [{}] ms", syncInfo.getId(), stopWatch.getTotalTimeMillis());
            } catch (ApiException | IOException e) {
                log.error("sync id [{}] task failed", syncInfo.getId());
                throw new RuntimeException(e);
            }
            return;
        }

    }


}
