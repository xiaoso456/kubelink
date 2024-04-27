package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;
import static io.github.xiaoso456.kubelink.constant.CommonConstant.FIRST_CONTAINER;

@Service
public class StatefulsetService {

    @Autowired
    ConfigManagementService configManagementService;



    public List<V1StatefulSet> listStatefulsets(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {

            V1StatefulSetList statefulSetList;
            if(StrUtil.isBlank(namespace) || ALL_NAMESPACE.equals(namespace)){
                statefulSetList = appsV1Api.listStatefulSetForAllNamespaces().execute();
            }else{
                statefulSetList = appsV1Api.listNamespacedStatefulSet(namespace).execute();
            }
            return statefulSetList.getItems();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    private static final List<String> SUSPEND_COMMANDS = List.of("/bin/sh", "-c");
    private static final List<String> SUSPEND_ARGS= List.of("while true; do echo 'suspend looping...'; sleep 5; done");

    public void suspendsSatefulset(String namespace,String deploymentName,String containerName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);


        try {
            V1StatefulSet v1StatefulSet = appsV1Api.readNamespacedStatefulSet(deploymentName, namespace).execute();
            V1PodSpec spec = v1StatefulSet.getSpec().getTemplate().getSpec();
            List<V1Container> containers = spec.getContainers();
            if(StrUtil.isBlank(containerName) || FIRST_CONTAINER.equals(containerName)){
                containers.get(0).setCommand(SUSPEND_COMMANDS);
                containers.get(0).setArgs(SUSPEND_ARGS);
            }else{
                containers.stream().filter(container -> container.getName().equals(containerName)).forEach(container -> {
                    container.setCommand(SUSPEND_COMMANDS);
                    container.setArgs(SUSPEND_ARGS);
                });
            }
            appsV1Api.replaceNamespacedStatefulSet(deploymentName, namespace, v1StatefulSet).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

}
