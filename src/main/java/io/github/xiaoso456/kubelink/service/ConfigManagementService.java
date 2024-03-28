package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.domain.ClusterConfig;
import io.github.xiaoso456.kubelink.exception.LinkException;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ConfigManagementService {

    @Autowired
    private ClusterConfigService clusterConfigService;

    private Long activeConfigId;

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
            log.info("active configs:{}",configId);
        } catch (IOException ioException) {
            log.error("create api client error", ioException);
            throw new LinkException(ioException);
        }
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

}
