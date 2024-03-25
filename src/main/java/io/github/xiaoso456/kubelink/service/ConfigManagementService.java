package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.LinkException;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ConfigManagementService {

    private Map<String, String> nameConfigMap = new ConcurrentHashMap<>();

    private String activeName;

    private ApiClient apiClient;

    public String getActiveName(){
        return activeName;
    }

    public ApiClient getApiClient(){
        return apiClient;
    }

    public List<String> getConfigNameList(){
        return new ArrayList<>(nameConfigMap.keySet());
    }

    public void createOrUpdateConfig(String configName, String config) {
        nameConfigMap.put(configName, config);
    }

    public boolean removeConfig(String configName) {
        return nameConfigMap.remove(configName) != null;
    }

    public void activeConfig(String configName) throws LinkException {
        String config = nameConfigMap.getOrDefault(configName, null);
        if (StrUtil.isBlank(config)) {
            throw new LinkException("configs is blank");
        }
        try {
            ApiClient client = KubeApiUtils.createApiClient(config);
            this.apiClient = client;
            this.activeName = configName;
            log.info("active configs:{}",activeName);
        } catch (IOException ioException) {
            log.error("create api client error", ioException);
            throw new LinkException(ioException);
        }
    }

}
