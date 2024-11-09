package io.github.xiaoso456.kubelink.domain.helm;

import io.github.xiaoso456.kubelink.domain.helm.release.Info;
import io.github.xiaoso456.kubelink.domain.helm.release.Chart;

import lombok.Data;
import java.util.Map;

@Data
public class Release {
    private String name;
    private Info info;
    private Chart chart;
    private Map<String, Object> config;
    private String manifest;
    private long version;
    private String namespace;
}
