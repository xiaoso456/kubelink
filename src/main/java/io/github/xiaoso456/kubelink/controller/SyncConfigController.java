package io.github.xiaoso456.kubelink.controller;

import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.domain.SyncConfig;
import io.github.xiaoso456.kubelink.domain.SyncResponse;
import io.github.xiaoso456.kubelink.domain.file.FileInfo;
import io.github.xiaoso456.kubelink.enums.SyncType;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.github.xiaoso456.kubelink.service.SyncConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sync")
public class SyncConfigController {

    @Autowired
    private SyncConfigService syncConfigService;

    @GetMapping("/config/{id}")
    public SyncConfig get(@PathVariable Long id) {
        return syncConfigService.getById(id);
    }

    @GetMapping("/config/list")
    public List<SyncConfig> list() {
        return syncConfigService.list();
    }

    @PostMapping("/config")
    public boolean add(@RequestBody SyncConfig syncConfig) {
        if(StrUtil.isBlank(syncConfig.getContainer())){
            syncConfig.setContainer("");
        }
        return syncConfigService.save(syncConfig);
    }

    @PutMapping("/config/{id}")
    public boolean update(@PathVariable Long id,@RequestBody SyncConfig syncConfig) {
        syncConfig.setId(id);
        if(StrUtil.isBlank(syncConfig.getContainer())){
            syncConfig.setContainer("");
        }
        return syncConfigService.updateById(syncConfig);
    }

    @DeleteMapping("/config/{id}")
    public boolean delete(@PathVariable Long id) {
        return syncConfigService.removeById(id);
    }

    @PostMapping("/config/{id}/sync-only")
    public SyncResponse syncOnly(@PathVariable Long id) {
        SyncConfig syncConfig = syncConfigService.getById(id);
        return syncConfigService.syncOnly(syncConfig);
    }

    @PostMapping("/config/{id}/delete-resource/{type}")
    public void deleteResource(@PathVariable Long id, @PathVariable String type) {
        // delete source or target
        if(!"source".equals(type) && !"target".equals(type)){
            throw new LinkRuntimeException("type must be source or target");
        }
        syncConfigService.deleteResource(id,type);
    }

    @GetMapping("/sync-type/list")
    public List<SyncType> listSyncType(){
        return Arrays.asList(SyncType.values());
    }


    @PostMapping("/namespace/{namespace}/pod/{pod}/container/{container}")
    public void active(@PathVariable String namespace, @PathVariable String pod, @PathVariable String container) {

    }

    @PostMapping("/local/path")
    public List<FileInfo> getLocalPath(@RequestBody String path){

        List<FileInfo> result = new ArrayList<>();
        File[] files;
        if("/".equals(path)){
            files = File.listRoots();
        }else{
            files = new File(path).listFiles();
        }
        if (files == null){
            return result;
        }
        for(File file:files){
            FileInfo fileInfo = FileInfo.builder()
                    .name("".equals(file.getName())?file.getPath():file.getName())
                    .dir(file.isDirectory())
                    .build();
            result.add(fileInfo);
        }
        return result;

    }

    @PostMapping("/namespace/{namespace}/pod/{pod}/container/{container}/path")
    public List<FileInfo> getRemotePath(@PathVariable String namespace,@PathVariable String pod,@PathVariable String container,@RequestBody String path){
        return syncConfigService.getPodPath(namespace,pod,container,path);
    }





}
