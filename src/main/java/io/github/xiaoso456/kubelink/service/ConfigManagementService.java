package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.domain.ClusterConfig;
import io.github.xiaoso456.kubelink.domain.ConnectionStatus;
import io.github.xiaoso456.kubelink.exception.LinkException;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.VersionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ConfigManagementService {

    @Autowired
    private ClusterConfigService clusterConfigService;

    private Long activeConfigId;

    private ConnectionStatus connectionStatus;

    private ApiClient apiClient;

    public void activeConfig(Long configId) throws LinkException {
        ClusterConfig clusterConfig = clusterConfigService.getById(configId);
        String config = clusterConfig.getConfig();
        if (StrUtil.isBlank(config)) {
            throw new LinkException("configs is blank");
        }
        try {
            ApiClient client = KubeApiUtils.createApiClient(config);
            this.apiClient = client;
            this.activeConfigId = configId;
            scheduleCheckConnectionStatus();
            log.info("active configs:{}",configId);
        } catch (IOException ioException) {
            log.error("create api client error", ioException);
            throw new LinkException(ioException);
        }
    }

    public ConnectionStatus getCurrentConnectionStatus() {
        return connectionStatus;
    }

    @Scheduled(fixedRate = 10000)
    public void scheduleCheckConnectionStatus(){
        if (activeConfigId == null) {
            this.connectionStatus = ConnectionStatus.builder()
                    .id(null)
                    .name("-")
                    .status(ConnectionStatus.UNKNOWN)
                    .clusterVersionInfo(null)
                    .build();
            return;
        }

        ClusterConfig clusterConfig = clusterConfigService.getById(activeConfigId);
        String status = ConnectionStatus.UNKNOWN;
        VersionInfo clusterVersionInfo = null;
        try {
            clusterVersionInfo = clusterConfigService.connect(activeConfigId);
            status = ConnectionStatus.HEALTHY;
        } catch (LinkException e) {
            status = ConnectionStatus.UNHEALTHY;
        }
        ConnectionStatus connectionStatus = ConnectionStatus.builder()
                .id(activeConfigId)
                .name(clusterConfig.getName())
                .status(status)
                .clusterVersionInfo(clusterVersionInfo)
                .build();
        this.connectionStatus = connectionStatus;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

}
