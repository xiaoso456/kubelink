package io.github.xiaoso456.kubelink.utils;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

public class KubeApiUtils {
    public static ApiClient createApiClient(String clusterConfig) throws IOException {
        StringReader stringReader = new StringReader(clusterConfig);
        KubeConfig kubeConfig = KubeConfig.loadKubeConfig(stringReader);
        return ClientBuilder.kubeconfig(kubeConfig)
                .setVerifyingSsl(false)
                .build();
    }

    public static String labelsMapToString(Map<String,String> labels) {

        //转化为k:v;k2:v2格式
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
