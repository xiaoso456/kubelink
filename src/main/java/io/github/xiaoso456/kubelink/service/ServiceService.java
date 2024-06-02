package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreApi;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;
import static io.github.xiaoso456.kubelink.constant.CommonConstant.FIRST_CONTAINER;

@Service
public class ServiceService {

    @Autowired
    ConfigManagementService configManagementService;

    public List<V1Service> listServices(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1ServiceList serviceList;
            if(StrUtil.isBlank(namespace) || ALL_NAMESPACE.equals(namespace)){
                serviceList = coreV1Api.listServiceForAllNamespaces().execute();
            }else{
                serviceList = coreV1Api.listNamespacedService(namespace).execute();
            }
            return serviceList.getItems();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Service getService(String namespace,String service){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            return coreV1Api.readNamespacedService(service, namespace).execute();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

}
