package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;

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

    public V1Service deleteService(String namespace, String serviceName){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Service v1Service = coreV1Api.deleteNamespacedService(serviceName, namespace).execute();
            return v1Service;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Service updateService(String namespace, String serviceName,V1Service v1Service){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            // TODO use patch instead of put
            V1Service serviceOld = coreV1Api.readNamespacedService(serviceName, namespace).execute();
            serviceOld.setMetadata(v1Service.getMetadata());
            serviceOld.setSpec(v1Service.getSpec());
            serviceOld.setStatus(v1Service.getStatus());
            V1Service v1ServiceNew = coreV1Api.replaceNamespacedService(serviceName, namespace, serviceOld).execute();
            return v1ServiceNew;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

}
