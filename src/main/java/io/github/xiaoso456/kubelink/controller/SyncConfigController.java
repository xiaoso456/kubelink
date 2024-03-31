package io.github.xiaoso456.kubelink.controller;

import io.github.xiaoso456.kubelink.domain.ClusterConfig;
import io.github.xiaoso456.kubelink.domain.SyncConfig;
import io.github.xiaoso456.kubelink.enums.SyncType;
import io.github.xiaoso456.kubelink.exception.LinkException;
import io.github.xiaoso456.kubelink.service.ClusterConfigService;
import io.github.xiaoso456.kubelink.service.SyncConfigService;
import io.kubernetes.client.openapi.models.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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
        return syncConfigService.save(syncConfig);
    }

    @PutMapping("/config/{id}")
    public boolean update(@PathVariable Long id,@RequestBody SyncConfig syncConfig) {
        syncConfig.setId(id);
        return syncConfigService.updateById(syncConfig);
    }

    @DeleteMapping("/config/{id}")
    public boolean delete(@PathVariable Long id) {
        return syncConfigService.removeById(id);
    }

    @PostMapping("/config/{id}/sync-only")
    public void syncOnly(@PathVariable Long id) {
        SyncConfig syncConfig = syncConfigService.getById(id);
        syncConfigService.syncOnly(syncConfig);
    }

    @GetMapping("/sync-type/list")
    public List<SyncType> listSyncType(){
        return Arrays.asList(SyncType.values());
    }



}
