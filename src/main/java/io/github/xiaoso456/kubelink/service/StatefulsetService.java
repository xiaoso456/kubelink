package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
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

    public List<V1Pod> getStatefulsetPods(String namespace, String statefulsetName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);
        try {
            V1StatefulSet v1StatefulSet = appsV1Api.readNamespacedStatefulSet(statefulsetName, namespace).execute();

            // String appLabel = v1StatefulSet.getSpec().getTemplate().getMetadata().getLabels().getOrDefault("app", "");
            // V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace).labelSelector("app=" + appLabel).execute();
            String selector = KubeApiUtils.labelsMapToString(v1StatefulSet.getSpec().getTemplate().getMetadata().getLabels());
            V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace).labelSelector(selector).execute();

            return v1PodList.getItems();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1StatefulSet getStatefulset(String namespace,String statefulset){
        ApiClient apiClient = configManagementService.getApiClient();



        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {
            return appsV1Api.readNamespacedStatefulSet(statefulset, namespace).execute();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public List<V1Service> getStatefulsetServices(String namespace, String statefulsetName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1StatefulSet v1StatefulSet = appsV1Api.readNamespacedStatefulSet(statefulsetName, namespace).execute();

            Map<String, String> v1StatefulSetLabels= v1StatefulSet.getSpec().getTemplate().getMetadata().getLabels();

            V1ServiceList v1ServiceList = coreV1Api.listNamespacedService(namespace).execute();
            List<V1Service> v1Services = v1ServiceList.getItems().stream().filter(v1Service -> {
                if(v1Service.getSpec().getSelector().isEmpty()){
                    return false;
                }
                boolean isMatch = true;
                for (Map.Entry<String, String> selectorLabel : v1Service.getSpec().getSelector().entrySet()) {
                    if (!v1StatefulSetLabels.containsKey(selectorLabel.getKey()) || !v1StatefulSetLabels.get(selectorLabel.getKey()).equals(selectorLabel.getValue())) {
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

    public String getStatefulsetYaml(String namespace, String statefulsetName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {
            V1StatefulSet v1StatefulSet = appsV1Api.readNamespacedStatefulSet(statefulsetName, namespace).execute();
            v1StatefulSet.getMetadata().setManagedFields(null);
            v1StatefulSet.setStatus(null);
            String yaml = Yaml.dump(v1StatefulSet);
            return yaml;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public void updateStatefulsetYaml(String namespace, String statefulsetName,String yaml){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {

            V1StatefulSet v1StatefulSet = Yaml.loadAs(yaml, V1StatefulSet.class);
            appsV1Api.replaceNamespacedStatefulSet(statefulsetName, namespace, v1StatefulSet).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Status deleteStatefulset(String namespace, String statefulstName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Status v1Status = appsV1Api.deleteNamespacedStatefulSet(statefulstName, namespace).execute();
            return v1Status;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    private static final List<String> SUSPEND_COMMANDS = List.of("/bin/sh", "-c");
    private static final List<String> SUSPEND_ARGS= List.of("while true; do echo 'suspend looping...'; sleep 5; done");

    public void suspendsSatefulset(String namespace,String statefulsetName,String containerName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);


        try {
            V1StatefulSet v1StatefulSet = appsV1Api.readNamespacedStatefulSet(statefulsetName, namespace).execute();
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
            appsV1Api.replaceNamespacedStatefulSet(statefulsetName, namespace, v1StatefulSet).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

}
