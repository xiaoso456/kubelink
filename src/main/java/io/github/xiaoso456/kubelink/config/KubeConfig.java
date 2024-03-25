package io.github.xiaoso456.kubelink.config;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.xiaoso456.kubelink.domain.KubeLinkProperties;
import io.github.xiaoso456.kubelink.domain.SyncInfo;
import io.github.xiaoso456.kubelink.service.ConfigManagementService;
import io.github.xiaoso456.kubelink.service.SyncManagementService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
@Data
public class KubeConfig implements InitializingBean {

    @Autowired
    ConfigManagementService configManagementService;

    @Autowired
    SyncManagementService syncManagementService;

    @Autowired
    KubeLinkProperties kubeLinkProperties;

    public static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        String jarPath = System.getProperty("user.dir");
        File configDir = new File(kubeLinkProperties.getConfig().getDirectory());
        log.info("Current startup path :{}", jarPath);
        log.info("KubeLink configs folder :{}", configDir.getAbsolutePath());
        if (!configDir.exists() || configDir.isFile()) {
            log.warn("Config dir not found ,start to create in [{}]", configDir.getAbsolutePath());
            configDir.mkdirs();
        }

        File[] files = configDir.listFiles();
        if (files == null || files.length == 0) {
            log.warn("Config file not found");
        } else {
            for (File configFile : files) {
                configManagementService.createOrUpdateConfig(configFile.getName(), FileUtil.readUtf8String(configFile));
                String defaultActiveConfig = kubeLinkProperties.getConfig().getDefaultActiveFileName();
                if (Objects.equals(defaultActiveConfig, configFile.getName())) {
                    configManagementService.activeConfig(defaultActiveConfig);
                }
                log.info("Success to load configs:[{}]", configFile.getName());

            }
        }


        File syncConfig = new File(kubeLinkProperties.getSync().getFile());
        log.info("KubeLink sync configs: [{}]", syncConfig.getAbsolutePath());
        TypeReference<List<SyncInfo>> listTypeReference = new TypeReference<>() {};
        List<SyncInfo> syncInfoList = objectMapper.readValue(syncConfig, listTypeReference);
        for (SyncInfo syncInfo : syncInfoList) {
            syncManagementService.createOrUpdateSyncInfo(syncInfo);
        }


    }
}
