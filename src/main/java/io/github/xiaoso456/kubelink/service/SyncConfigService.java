package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.xiaoso456.kubelink.api.Copy;
import io.github.xiaoso456.kubelink.constant.CommonConstant;
import io.github.xiaoso456.kubelink.domain.SyncConfig;
import io.github.xiaoso456.kubelink.domain.SyncResponse;
import io.github.xiaoso456.kubelink.domain.file.FileInfo;
import io.github.xiaoso456.kubelink.enums.SyncType;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.github.xiaoso456.kubelink.mapper.SyncConfigMapper;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.exception.CopyNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
public class SyncConfigService extends ServiceImpl<SyncConfigMapper, SyncConfig> {

    @Autowired
    private ConfigManagementService configManagementService;
    
    private Map<Long, SyncConfig> idSyncConfigMap = new ConcurrentHashMap<>();
    private Map<Long, WatchMonitor> idWatchMonitor = new ConcurrentHashMap<>();

    public void activeAutoSync(SyncConfig syncConfig) {

        removeSyncTask(syncConfig.getId());
        idSyncConfigMap.put(syncConfig.getId(), syncConfig);

        if(syncConfig.getAutoSync() && syncConfig.getEnable() && syncConfig.getSyncType() == SyncType.FILE_LOCAL_TO_POD){
            File watchFile = new File(syncConfig.getSource());
            WatchMonitor watchMonitor = WatchMonitor.create(watchFile, WatchMonitor.EVENTS_ALL);
            watchMonitor.setWatcher(new Watcher() {
                @Override
                public void onCreate(WatchEvent<?> event, Path currentPath) {
                    log.info("sync id [{}], file create, start to sync.", syncConfig.getId());
                    syncOnly(syncConfig);
                }

                @Override
                public void onModify(WatchEvent<?> event, Path currentPath) {
                    log.info("sync id [{}], file modify, start to sync.", syncConfig.getId());
                    syncOnly(syncConfig);
                }

                @Override
                public void onDelete(WatchEvent<?> event, Path currentPath) {
                    // todo remove or do nothing?
                }

                @Override
                public void onOverflow(WatchEvent<?> event, Path currentPath) {
                    log.error("watch id [{}] failed, please restart", syncConfig.getId());
                }
            });

            watchMonitor.start();
            idWatchMonitor.put(syncConfig.getId(),watchMonitor);
        }
    }

    public boolean removeSyncTask(Long id) {
        SyncConfig remove = idSyncConfigMap.remove(id);
        WatchMonitor watchMonitor = idWatchMonitor.remove(id);
        if(watchMonitor != null){
            watchMonitor.close();
        }
        return remove != null;
    }


    public SyncResponse syncOnly(SyncConfig syncConfig){
        ApiClient apiClient = configManagementService.getApiClient();
        Copy copy = new Copy();
        copy.setApiClient(apiClient);
        StopWatch stopWatch = StopWatch.create(UUID.fastUUID().toString());
        stopWatch.start();
        if (syncConfig.getSyncType() == SyncType.FILE_LOCAL_TO_POD && syncConfig.getEnable()) {
            try {
                copy.copyFileToPod(syncConfig.getNamespace(), syncConfig.getPod(), syncConfig.getContainer(), Paths.get(syncConfig.getSource()), Paths.get(syncConfig.getTarget()));
                stopWatch.stop();
                log.info("Sync id [{}] task success,cost [{}] ms", syncConfig.getId(), stopWatch.getTotalTimeMillis());
            } catch (Exception e) {
                log.error("Sync id [{}] task failed", syncConfig.getId());
                throw new LinkRuntimeException(e);
            }
            return SyncResponse.builder()
                    .success(true)
                    .spendMillisecond(stopWatch.getTotalTimeMillis())
                    .build();

        }

        if(syncConfig.getSyncType() == SyncType.FILE_POD_TO_LOCAL && syncConfig.getEnable()){
            try {
                copy.copyFileFromPod(syncConfig.getNamespace(), syncConfig.getPod(), syncConfig.getContainer(), syncConfig.getSource(), Paths.get(syncConfig.getTarget()));
                stopWatch.stop();
                log.info("Sync id [{}] task success,cost [{}] ms", syncConfig.getId(), stopWatch.getTotalTimeMillis());
            } catch (ApiException | IOException e) {
                log.error("sync id [{}] task failed", syncConfig.getId());
                throw new LinkRuntimeException(e);
            }
            return SyncResponse.builder()
                    .success(true)
                    .spendMillisecond(stopWatch.getTotalTimeMillis())
                    .build();

        }

        if(syncConfig.getSyncType() == SyncType.FOLDER_POD_TO_LOCAL && syncConfig.getEnable()){
            try {
                copy.copyDirectoryDirectFromPod(syncConfig.getNamespace(), syncConfig.getPod(), syncConfig.getContainer(), syncConfig.getSource(), Paths.get(syncConfig.getTarget()));
                stopWatch.stop();
                log.info("Sync id [{}] task success,cost [{}] ms", syncConfig.getId(), stopWatch.getTotalTimeMillis());
            } catch (ApiException | IOException | CopyNotSupportedException e) {
                log.error("sync id [{}] task failed", syncConfig.getId());
                throw new LinkRuntimeException(e);
            }
            return SyncResponse.builder()
                    .success(true)
                    .spendMillisecond(stopWatch.getTotalTimeMillis())
                    .build();
        }

        if(syncConfig.getSyncType() == SyncType.FOLDER_LOCAL_TO_POD && syncConfig.getEnable()){
            try {
                copy.copyDirectoryToPod(syncConfig.getNamespace(),syncConfig.getPod(),syncConfig.getContainer(),Paths.get(syncConfig.getSource()), Paths.get(syncConfig.getTarget()));
                stopWatch.stop();
                return SyncResponse.builder()
                        .success(true)
                        .spendMillisecond(stopWatch.getTotalTimeMillis())
                        .build();
            }catch (IOException | ApiException e) {
                throw new LinkRuntimeException(e);
            }

        }
        return SyncResponse.builder()
                .success(false)
                .spendMillisecond(-1L)
                .build();

    }

    public void clearAll(){
        idSyncConfigMap.clear();
        for(WatchMonitor watchMonitor:idWatchMonitor.values()){
            watchMonitor.close();
        }
        idWatchMonitor.clear();
    }

    public void deleteResource(Long id,String type){
        SyncConfig syncConfig = getById(id);
        boolean removeLocal = false;
        if(syncConfig.getSyncType() == SyncType.FILE_LOCAL_TO_POD && "source".equals(type)){
            removeLocal = true;
        }else if(syncConfig.getSyncType() == SyncType.FOLDER_LOCAL_TO_POD && "source".equals(type)){
            removeLocal = true;
        }else if(syncConfig.getSyncType() == SyncType.FILE_POD_TO_LOCAL && "target".equals(type)){
            removeLocal = true;
        }else if(syncConfig.getSyncType() == SyncType.FOLDER_POD_TO_LOCAL && "target".equals(type)){
            removeLocal = true;
        }

        if(removeLocal){
            File file = new File(syncConfig.getSource());
            if(file.exists()){
                file.delete();
            }
        }else {
            ApiClient apiClient = configManagementService.getApiClient();
            Exec exec = new Exec();
            exec.setApiClient(apiClient);
            try {
                Process process = exec.exec(syncConfig.getNamespace(), syncConfig.getPod(), new String[]{"rm", "-rf", syncConfig.getTarget()}, false, false);
                process.waitFor();
            } catch (IOException | ApiException | InterruptedException e) {
                throw new LinkRuntimeException(e);
            }
        }
    }

    public List<FileInfo> getPodPath(String namespace,String pod,String container,String path){
        if(CommonConstant.ROOT_FLAG.equals(path)){
            path = "/";
        }
        ApiClient apiClient = configManagementService.getApiClient();
        apiClient.setVerifyingSsl(false);
        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        Exec exec = new Exec();
        exec.setApiClient(apiClient);
        try{
            Process process;
            if(CommonConstant.NULL.equals(container) || CommonConstant.FIRST_CONTAINER.equals(container)){
                process = exec.exec(namespace, pod, new String[]{"ls", "-l", path}, true, false);
            }else{
                process = exec.exec(namespace, pod, new String[]{"ls", "-l", path}, container, true, false);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<FileInfo> fileInfos = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 9) {
                    boolean isDir = parts[0].charAt(0) == 'd';
                    String name;

                    if (parts[0].startsWith("l")) {
                        name = parts[parts.length-3];
                    }else{
                        name = parts[parts.length-1];
                    }

                    FileInfo fileInfo = FileInfo.builder()
                            .name(name)
                            .dir(isDir)
                            .build();
                    fileInfos.add(fileInfo);
                }
            }

            process.waitFor();
            process.destroy();
            return fileInfos;


        } catch (Exception e) {
            throw new LinkRuntimeException(e);
        }





    }

}




