package io.github.xiaoso456.kubelink.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "kube.link", ignoreUnknownFields = true)
@Data
public class KubeLinkProperties {

    private Config config;

    private Sync sync;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config{
        private String directory;
        private String defaultActiveFileName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sync{
        private String file;
    }
}
