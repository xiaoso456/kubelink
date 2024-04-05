package io.github.xiaoso456.kubelink.service;


import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NamespaceService {

    @Autowired
    ConfigManagementService configManagementService;

    public List<V1Namespace> listNamespace(){
        ApiClient apiClient = configManagementService.getApiClient();


        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);
        try {
            V1NamespaceList namespaceList = coreV1Api.listNamespace().execute();
            return namespaceList.getItems();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public List<V1Pod> listPods(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();
        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);
        try {
            return coreV1Api.listNamespacedPod(namespace).execute().getItems();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public List<V1Container> listPodContainers(String namespace, String pod){
        ApiClient apiClient = configManagementService.getApiClient();
        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);
        try {
            V1Pod v1Pod = coreV1Api.readNamespacedPod(pod, namespace).execute();
            return v1Pod.getSpec().getContainers();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }


}
