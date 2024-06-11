package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;

@Service
public class ConfigMapService {

    @Autowired
    ConfigManagementService configManagementService;

    public List<V1ConfigMap> listConfigMaps(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1ConfigMapList configMapList;
            if(StrUtil.isBlank(namespace) || ALL_NAMESPACE.equals(namespace)){
                configMapList = coreV1Api.listConfigMapForAllNamespaces().execute();
            }else{
                configMapList = coreV1Api.listNamespacedConfigMap(namespace).execute();
            }
            return configMapList.getItems();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1ConfigMap getConfigMap(String namespace,String configmap){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            return coreV1Api.readNamespacedConfigMap(configmap, namespace).execute();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public V1Status deleteConfigMap(String namespace, String configmapName){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Status v1Status = coreV1Api.deleteNamespacedConfigMap(configmapName, namespace).execute();
            return v1Status;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1ConfigMap updateConfigMap(String namespace, String configmapName,V1ConfigMap v1ConfigMap){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            // TODO use patch instead of put
            V1ConfigMap configMapOld = coreV1Api.readNamespacedConfigMap(configmapName, namespace).execute();
            configMapOld.setMetadata(v1ConfigMap.getMetadata());
            configMapOld.setData(v1ConfigMap.getData());

            V1ConfigMap v1ConfigMapNew = coreV1Api.replaceNamespacedConfigMap(configmapName, namespace, configMapOld).execute();
            return v1ConfigMapNew;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1ConfigMap createConfigMap(String namespace,V1ConfigMap v1ConfigMap){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1ConfigMap v1ConfigMapNew = coreV1Api.createNamespacedConfigMap(namespace, v1ConfigMap).execute();

            return v1ConfigMapNew;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public String getConfigmapYaml(String namespace, String configmapName){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1ConfigMap v1ConfigMap = coreV1Api.readNamespacedConfigMap(configmapName, namespace).execute();
            v1ConfigMap.getMetadata().setManagedFields(null);
            String yaml = Yaml.dump(v1ConfigMap);
            return yaml;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public void updateConfigmapYaml(String namespace, String configmapName,String yaml){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {

            V1ConfigMap v1ConfigMap = Yaml.loadAs(yaml, V1ConfigMap.class);
            coreV1Api.replaceNamespacedConfigMap(configmapName, namespace, v1ConfigMap).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }


}
