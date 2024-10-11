package io.github.xiaoso456.kubelink.utils;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if(sb.length() > 0){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static <T> String toJsonString(List<T> objs) {

        return "[" +
                    objs.stream().map(obj -> {

                                if(obj instanceof V1Pod objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof V1Service objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof V1Deployment objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof V1StatefulSet objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof V1DaemonSet objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof V1Job objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof V1ConfigMap objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof V1Secret objTemp){
                                    return objTemp.toJson();
                                }else if(obj instanceof CoreV1Event objTemp){
                                    return objTemp.toJson();
                                }else {
                                    throw new RuntimeException("not support type");
                                }
                            } )
                            .collect(Collectors.joining(",")) +
                "]";
    }


}
