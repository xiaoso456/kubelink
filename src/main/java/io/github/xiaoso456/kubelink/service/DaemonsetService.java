package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;
import static io.github.xiaoso456.kubelink.constant.CommonConstant.FIRST_CONTAINER;

@Service
public class DaemonsetService {

    @Autowired
    ConfigManagementService configManagementService;



    public List<V1DaemonSet> listDaemonsets(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {

            V1DaemonSetList daemonsetList;
            if(StrUtil.isBlank(namespace) || ALL_NAMESPACE.equals(namespace)){
                daemonsetList = appsV1Api.listDaemonSetForAllNamespaces().execute();
            }else{
                daemonsetList = appsV1Api.listNamespacedDaemonSet(namespace).execute();
            }
            return daemonsetList.getItems();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public List<V1Service> getDaemonsetServices(String namespace, String daemonsetName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1DaemonSet v1DaemonSet = appsV1Api.readNamespacedDaemonSet(daemonsetName, namespace).execute();

            Map<String, String> daemonsetLabels= v1DaemonSet.getSpec().getTemplate().getMetadata().getLabels();

            V1ServiceList v1ServiceList = coreV1Api.listNamespacedService(namespace).execute();
            List<V1Service> v1Services = v1ServiceList.getItems().stream().filter(v1Service -> {
                if(v1Service.getSpec().getSelector().isEmpty()){
                    return false;
                }
                boolean isMatch = true;
                for (Map.Entry<String, String> selectorLabel : v1Service.getSpec().getSelector().entrySet()) {
                    if (!daemonsetLabels.containsKey(selectorLabel.getKey()) || !daemonsetLabels.get(selectorLabel.getKey()).equals(selectorLabel.getValue())) {
                        isMatch = false;
                        break;
                    }
                }
                return isMatch;

            }).toList();

            return v1Services;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }



    public List<V1Pod> getDaemonsetPods(String namespace, String daemonsetName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);
        try {
            V1DaemonSet v1Daemonset = appsV1Api.readNamespacedDaemonSet(daemonsetName, namespace).execute();

            String selector = KubeApiUtils.labelsMapToString(v1Daemonset.getSpec().getTemplate().getMetadata().getLabels());
            V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace).labelSelector(selector).execute();

            return v1PodList.getItems();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1DaemonSet getDaemonsetset(String namespace,String daemonset){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {
            return appsV1Api.readNamespacedDaemonSet(daemonset, namespace).execute();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }


    private static final List<String> SUSPEND_COMMANDS = List.of("/bin/sh", "-c");
    private static final List<String> SUSPEND_ARGS= List.of("while true; do echo 'suspend looping...'; sleep 5; done");

    public void suspendsDaemonset(String namespace,String deploymentName,String containerName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);


        try {
            V1DaemonSet v1Daemonset = appsV1Api.readNamespacedDaemonSet(deploymentName, namespace).execute();
            V1PodSpec spec = v1Daemonset.getSpec().getTemplate().getSpec();
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

            appsV1Api.replaceNamespacedDaemonSet(deploymentName, namespace, v1Daemonset).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

}
