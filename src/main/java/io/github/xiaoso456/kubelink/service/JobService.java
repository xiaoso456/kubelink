package io.github.xiaoso456.kubelink.service;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreApi;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;

@Service
public class JobService {

    @Autowired
    ConfigManagementService configManagementService;

    public List<V1Job> listJobs(String namespace){
        ApiClient apiClient = configManagementService.getApiClient();

        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);


        try {
            V1JobList v1JobList;
            if(StrUtil.isBlank(namespace) || ALL_NAMESPACE.equals(namespace)){
                v1JobList = batchV1Api.listJobForAllNamespaces().execute();
            }else{
                v1JobList = batchV1Api.listNamespacedJob(namespace).execute();
            }
            return v1JobList.getItems();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Job getJob(String namespace,String job){
        ApiClient apiClient = configManagementService.getApiClient();

        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);

        try {
            return batchV1Api.readNamespacedJob(job, namespace).execute();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }

    }

    public List<V1Pod> getJobPods(String namespace, String jobName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Job v1Job = batchV1Api.readNamespacedJob(jobName, namespace).execute();

            String selector = KubeApiUtils.labelsMapToString(v1Job.getSpec().getTemplate().getMetadata().getLabels());
            V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace).labelSelector(selector).execute();

            return v1PodList.getItems();
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public List<V1Service> getJobServices(String namespace, String jobName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);
        try {
            V1Job v1Job = batchV1Api.readNamespacedJob(jobName, namespace).execute();
            Map<String, String> jobLabels= v1Job.getSpec().getTemplate().getMetadata().getLabels();

            V1ServiceList v1ServiceList = coreV1Api.listNamespacedService(namespace).execute();
            List<V1Service> v1Services = v1ServiceList.getItems().stream().filter(v1Service -> {
                if(v1Service.getSpec().getSelector().isEmpty()){
                    return false;
                }
                boolean isMatch = true;
                for (Map.Entry<String, String> selectorLabel : v1Service.getSpec().getSelector().entrySet()) {
                    if (!jobLabels.containsKey(selectorLabel.getKey()) || !jobLabels.get(selectorLabel.getKey()).equals(selectorLabel.getValue())) {
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

    public String getJobYaml(String namespace, String jobName){
        ApiClient apiClient = configManagementService.getApiClient();

        AppsV1Api appsV1Api = new AppsV1Api();
        appsV1Api.setApiClient(apiClient);

        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);

        try {

            V1Job v1Job = batchV1Api.readNamespacedJob(jobName, namespace).execute();
            v1Job.getMetadata().setManagedFields(null);
            v1Job.setStatus(null);
            String yaml = Yaml.dump(v1Job);
            return yaml;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public void updateJobYaml(String namespace, String jobName,String yaml){
        ApiClient apiClient = configManagementService.getApiClient();


        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);

        try {

            V1Job v1Job = Yaml.loadAs(yaml, V1Job.class);
            batchV1Api.replaceNamespacedJob(jobName, namespace, v1Job).execute();

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Status deleteJob(String namespace, String jobName){
        ApiClient apiClient = configManagementService.getApiClient();

        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            V1Status v1Status = batchV1Api.deleteNamespacedJob(jobName, namespace).execute();
            return v1Status;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }

    public V1Job updateJob(String namespace, String jobName,V1Job v1Job){
        ApiClient apiClient = configManagementService.getApiClient();


        BatchV1Api batchV1Api = new BatchV1Api();
        batchV1Api.setApiClient(apiClient);


        try {
            // TODO use patch instead of put
            V1Job jobOld = batchV1Api.readNamespacedJob(jobName, namespace).execute();
            jobOld.setMetadata(v1Job.getMetadata());
            jobOld.setSpec(v1Job.getSpec());
            jobOld.setStatus(v1Job.getStatus());
            V1Job jobNew = batchV1Api.replaceNamespacedJob(jobName, namespace, jobOld).execute();
            return jobNew;
        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }


}
