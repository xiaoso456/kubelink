package io.github.xiaoso456.kubelink.controller;

import io.github.xiaoso456.kubelink.domain.ClusterConfig;
import io.github.xiaoso456.kubelink.domain.ConnectionStatus;
import io.github.xiaoso456.kubelink.exception.LinkException;
import io.github.xiaoso456.kubelink.service.ClusterConfigService;
import io.github.xiaoso456.kubelink.service.ConfigManagementService;
import io.github.xiaoso456.kubelink.service.SyncConfigService;
import io.kubernetes.client.openapi.models.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cluster/config")
public class ClusterConfigController {

    @Autowired
    private ClusterConfigService clusterConfigService;

    @Autowired
    private ConfigManagementService configManagementService;

    @Autowired
    private SyncConfigService syncConfigService;

    @GetMapping("/{id}")
    public ClusterConfig get(@PathVariable Long id) {
        return clusterConfigService.getById(id);
    }

    @GetMapping("/list")
    public List<ClusterConfig> list() {
        return clusterConfigService.list();
    }

    @GetMapping("/current/status")
    public ConnectionStatus getCurrentConnectionStatus() {
        return configManagementService.getCurrentConnectionStatus();
    }

    @PostMapping
    public boolean add(@RequestBody ClusterConfig clusterConfig) {
        return clusterConfigService.save(clusterConfig);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,@RequestBody ClusterConfig clusterConfig) {
        clusterConfig.setId(id);
        return clusterConfigService.updateById(clusterConfig);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return clusterConfigService.removeById(id);
    }

    @GetMapping("/{id}/connect")
    public VersionInfo connect(@PathVariable Long id) throws LinkException {
        return clusterConfigService.connect(id);
    }

    @PutMapping("/{id}/active")
    public void active(@PathVariable Long id) throws LinkException {
        syncConfigService.clearAll();
        configManagementService.activeConfig(id);
    }

}
