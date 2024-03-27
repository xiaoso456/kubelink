package io.github.xiaoso456.kubelink.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.xiaoso456.kubelink.domain.ClusterConfig;
import io.github.xiaoso456.kubelink.exception.LinkException;
import io.github.xiaoso456.kubelink.mapper.ClusterConfigMapper;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.VersionApi;
import io.kubernetes.client.openapi.models.VersionInfo;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringReader;


@Service
@Slf4j
public class ClusterConfigService extends ServiceImpl<ClusterConfigMapper, ClusterConfig> {

    public VersionInfo connect(Long id) throws LinkException {

        ClusterConfig clusterConfig = baseMapper.selectById(id);
        String configString = clusterConfig.getConfig();
        StringReader stringReader = new StringReader(configString);

        KubeConfig kubeConfig = KubeConfig.loadKubeConfig(stringReader);
        try {
            ApiClient apiClient = ClientBuilder.kubeconfig(kubeConfig)
                    .setVerifyingSsl(false)
                    .build();
            VersionApi versionApi = new VersionApi();
            versionApi.setApiClient(apiClient);
            return versionApi.getCode().execute();
        } catch (Exception e) {
            log.error("Connect failed, config id [{}]", id, e);
            throw new LinkException(e);
        }


    }


}




