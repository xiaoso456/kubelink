package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.FIRST_CONTAINER;
import static io.github.xiaoso456.kubelink.constant.CommonConstant.NULL;

@Service
public class PodService {

    @Autowired
    ConfigManagementService configManagementService;



    public String getPodLogs(String namespace,String pod,String container,boolean previous,int tailLines){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api(apiClient);

        try {
            CoreV1Api.APIreadNamespacedPodLogRequest apiReadNamespacedPodLogRequest = coreV1Api.readNamespacedPodLog(pod, namespace);
            if(StrUtil.isBlank(container) || container.equals(FIRST_CONTAINER) || container.equals(NULL)){
                apiReadNamespacedPodLogRequest.container(null);
            }else{
                apiReadNamespacedPodLogRequest.container(container);
            }
            apiReadNamespacedPodLogRequest.previous(previous);
            apiReadNamespacedPodLogRequest.tailLines(tailLines);
            return apiReadNamespacedPodLogRequest.execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public V1Pod getPod(String namespace,String pod){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api(apiClient);

        try {
            V1Pod v1Pod = coreV1Api.readNamespacedPod(pod, namespace).execute();

            return v1Pod;

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public V1Pod deletePod(String namespace,String pod){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api(apiClient);

        try {
            V1Pod v1Pod = coreV1Api.deleteNamespacedPod(pod, namespace).execute();
            return v1Pod;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }



}
