package io.github.xiaoso456.kubelink.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SyncManagementService {

    // @Autowired
    // ConfigManagementService configManagementService;
    //
    // private Map<String, SyncConfig> idSyncInfoMap = new ConcurrentHashMap<>();
    // private Map<String, WatchMonitor> idWatchMonitor = new ConcurrentHashMap<>();
    //
    // public void createOrUpdateSyncInfo(SyncConfig syncConfig) {
    //     removeSyncInfo(syncConfig.getId());
    //     idSyncInfoMap.put(syncConfig.getId(), syncConfig);
    //     if(syncConfig.getAutoSync() && syncConfig.getEnable() && syncConfig.getSyncType() == SyncType.FILE_LOCAL_TO_POD){
    //         File watchFile = new File(syncConfig.getSource());
    //         WatchMonitor watchMonitor = WatchMonitor.create(watchFile, WatchMonitor.EVENTS_ALL);
    //         watchMonitor.setWatcher(new Watcher() {
    //             @Override
    //             public void onCreate(WatchEvent<?> event, Path currentPath) {
    //                 log.info("sync id [{}], file create, start to sync.", syncConfig.getId());
    //                 sync(syncConfig);
    //             }
    //
    //             @Override
    //             public void onModify(WatchEvent<?> event, Path currentPath) {
    //                 log.info("sync id [{}], file modify, start to sync.", syncConfig.getId());
    //                 sync(syncConfig);
    //             }
    //
    //             @Override
    //             public void onDelete(WatchEvent<?> event, Path currentPath) {
    //                 // todo remove or do nothing?
    //             }
    //
    //             @Override
    //             public void onOverflow(WatchEvent<?> event, Path currentPath) {
    //                 log.error("watch id [{}] failed, please restart", syncConfig.getId());
    //             }
    //         });
    //         watchMonitor.start();
    //         idWatchMonitor.put(syncConfig.getId(),watchMonitor);
    //     }
    // }
    //
    // public boolean removeSyncInfo(String id) {
    //     SyncConfig remove = idSyncInfoMap.remove(id);
    //     WatchMonitor watchMonitor = idWatchMonitor.remove(id);
    //     if(watchMonitor != null){
    //         watchMonitor.close();
    //     }
    //     return remove != null;
    // }
    //
    // public List<SyncConfig> listSyncInfo(){
    //     return new ArrayList<>(idSyncInfoMap.values());
    // }
    //
    // public SyncConfig getSyncInfo(String id){
    //     return idSyncInfoMap.get(id);
    // }
    //
    // public void sync(SyncConfig syncConfig) {
    //     ApiClient apiClient = configManagementService.getApiClient();
    //     Copy copy = new Copy();
    //     copy.setApiClient(apiClient);
    //     StopWatch stopWatch = StopWatch.create(UUID.fastUUID().toString());
    //     stopWatch.start();
    //     if (syncConfig.getSyncType() == SyncType.FILE_LOCAL_TO_POD && syncConfig.getEnable()) {
    //         try {
    //             copy.copyFileToPod(syncConfig.getNamespace(), syncConfig.getPod(), syncConfig.getContainer(), Paths.get(syncConfig.getSource()), Paths.get(syncConfig.getTarget()));
    //             stopWatch.stop();
    //             log.info("Sync id [{}] task success,cost [{}] ms", syncConfig.getId(), stopWatch.getTotalTimeMillis());
    //         } catch (ApiException | IOException e) {
    //             log.error("Sync id [{}] task failed", syncConfig.getId());
    //             throw new RuntimeException(e);
    //         }
    //         return;
    //     }
    //
    //     if(syncConfig.getSyncType() == SyncType.FILE_POD_TO_LOCAL && syncConfig.getEnable()){
    //         if (syncConfig.getAutoSync()){
    //             log.warn("Unsupported auto sync from pod to local, sync id [{}]", syncConfig.getId());
    //         }
    //         try {
    //             copy.copyFileFromPod(syncConfig.getNamespace(), syncConfig.getPod(), syncConfig.getContainer(), syncConfig.getSource(), Paths.get(syncConfig.getTarget()));
    //             stopWatch.stop();
    //             log.info("Sync id [{}] task success,cost [{}] ms", syncConfig.getId(), stopWatch.getTotalTimeMillis());
    //         } catch (ApiException | IOException e) {
    //             log.error("sync id [{}] task failed", syncConfig.getId());
    //             throw new RuntimeException(e);
    //         }
    //         return;
    //     }
    //
    //     if(syncConfig.getSyncType() == SyncType.FOLDER_POD_TO_LOCAL && syncConfig.getEnable()){
    //         if (syncConfig.getAutoSync()){
    //             log.warn("Unsupported auto sync from pod to local, sync id [{}]", syncConfig.getId());
    //         }
    //         try {
    //             copy.copyDirectoryFromPod(syncConfig.getNamespace(), syncConfig.getPod(), syncConfig.getContainer(), syncConfig.getSource(), Paths.get(syncConfig.getTarget()));
    //             stopWatch.stop();
    //             log.info("Sync id [{}] task success,cost [{}] ms", syncConfig.getId(), stopWatch.getTotalTimeMillis());
    //         } catch (ApiException | IOException | CopyNotSupportedException e) {
    //             log.error("sync id [{}] task failed", syncConfig.getId());
    //             throw new RuntimeException(e);
    //         }
    //         return;
    //     }
    //
    // }


}
