package io.github.xiaoso456.kubelink.utils;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.IOException;
import java.io.StringReader;

public class KubeApiUtils {
    public static ApiClient createApiClient(String clusterConfig) throws IOException {
        StringReader stringReader = new StringReader(clusterConfig);
        KubeConfig kubeConfig = KubeConfig.loadKubeConfig(stringReader);
        return ClientBuilder.kubeconfig(kubeConfig)
                .setVerifyingSsl(false)
                .build();
    }
}
