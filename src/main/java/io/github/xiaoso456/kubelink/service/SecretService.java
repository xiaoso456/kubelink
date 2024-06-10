package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;

@Service
public class SecretService {

    @Autowired
    ConfigManagementService configManagementService;

    public List<V1Secret> listSecrets(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1SecretList secretList;
            if(StrUtil.isBlank(namespace) || ALL_NAMESPACE.equals(namespace)){
                secretList = coreV1Api.listSecretForAllNamespaces().execute();
            }else{
                secretList = coreV1Api.listNamespacedSecret(namespace).execute();
            }
            return secretList.getItems();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Secret getSecret(String namespace,String secret){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            return coreV1Api.readNamespacedSecret(secret, namespace).execute();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public V1Status deleteSecret(String namespace, String secretName){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Status v1Status = coreV1Api.deleteNamespacedSecret(secretName, namespace).execute();
            return v1Status;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Secret updateSecret(String namespace, String secretName,V1Secret v1Secret){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            // TODO use patch instead of put
            V1Secret secretOld = coreV1Api.readNamespacedSecret(secretName, namespace).execute();
            secretOld.setMetadata(v1Secret.getMetadata());
            secretOld.setData(v1Secret.getData());

            V1Secret v1SecretNew = coreV1Api.replaceNamespacedSecret(secretName, namespace, secretOld).execute();
            return v1SecretNew;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public String getSecretYaml(String namespace, String secretName){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Secret v1Secret = coreV1Api.readNamespacedSecret(secretName, namespace).execute();
            v1Secret.getMetadata().setManagedFields(null);
            String yaml = Yaml.dump(v1Secret);
            return yaml;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public void updateSecretYaml(String namespace, String secretName,String yaml){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {

            V1Secret v1Secret = Yaml.loadAs(yaml, V1Secret.class);
            coreV1Api.replaceNamespacedSecret(secretName, namespace, v1Secret).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }


}
