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
public class DeploymentService {

    @Autowired
    ConfigManagementService configManagementService;



    public List<V1Deployment> listDeployments(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {
            V1DeploymentList deploymentList;
            if(StrUtil.isBlank(namespace) || ALL_NAMESPACE.equals(namespace)){
                deploymentList = appsV1Api.listDeploymentForAllNamespaces().execute();
            }else{
                deploymentList = appsV1Api.listNamespacedDeployment(namespace).execute();
            }
            return deploymentList.getItems();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Deployment getDeployment(String namespace,String deployment){
        ApiClient apiClient = configManagementService.getApiClient();



        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {
            return appsV1Api.readNamespacedDeployment(deployment, namespace).execute();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }


    private static final List<String> SUSPEND_COMMANDS = List.of("/bin/sh", "-c");
    private static final List<String> SUSPEND_ARGS= List.of("while true; do echo 'suspend looping...'; sleep 5; done");

    public void suspendDeployment(String namespace,String deploymentName,String containerName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);


        try {
            V1Deployment v1Deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace).execute();
            V1PodSpec spec = v1Deployment.getSpec().getTemplate().getSpec();
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
            appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, v1Deployment).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public List<V1Pod> getDeploymentPods(String namespace, String deploymentName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Deployment v1Deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace).execute();
            // String appLabel = v1Deployment.getSpec().getTemplate().getMetadata().getLabels().getOrDefault("app", "");
            // V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace).labelSelector("app=" + appLabel).execute();
            String selector = KubeApiUtils.labelsMapToString(v1Deployment.getSpec().getTemplate().getMetadata().getLabels());
            V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace).labelSelector(selector).execute();

            return v1PodList.getItems();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public List<V1Service> getDeploymentServices(String namespace, String deploymentName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Deployment v1Deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace).execute();

            Map<String, String> deploymentLabels= v1Deployment.getSpec().getTemplate().getMetadata().getLabels();

            V1ServiceList v1ServiceList = coreV1Api.listNamespacedService(namespace).execute();
            List<V1Service> v1Services = v1ServiceList.getItems().stream().filter(v1Service -> {
                if(v1Service.getSpec().getSelector().isEmpty()){
                    return false;
                }
                boolean isMatch = true;
                for (Map.Entry<String, String> selectorLabel : v1Service.getSpec().getSelector().entrySet()) {
                    if (!deploymentLabels.containsKey(selectorLabel.getKey()) || !deploymentLabels.get(selectorLabel.getKey()).equals(selectorLabel.getValue())) {
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

    public String getDeploymentYaml(String namespace, String deploymentName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {
            V1Deployment v1Deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace).execute();
            v1Deployment.getMetadata().setManagedFields(null);
            v1Deployment.setStatus(null);
            String yaml = Yaml.dump(v1Deployment);
            return yaml;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public void updateDeploymentYaml(String namespace, String deploymentName,String yaml){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        try {

            V1Deployment v1Deployment = Yaml.loadAs(yaml, V1Deployment.class);
            appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, v1Deployment).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    // public void showDeploymentHistory(String namespace,String deploymentName){
    //     ApiClient apiClient = configManagementService.getApiClient();
    //
    //     AppsV1Api appsV1Api = new AppsV1Api();
    //     appsV1Api.setApiClient(apiClient);
    //     try {
    //         V1Deployment v1Deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace).execute();
    //         V1Deployment execute = appsV1Api.readNamespacedDeploymentStatus(deploymentName, namespace).execute();
    //         ApiextensionsV1Api apiextensionsV1Api = new ApiextensionsV1Api();
    //
    //
    //     } catch (ApiException e) {
    //         throw new LinkRuntimeException(e);
    //     }
    // }



}
