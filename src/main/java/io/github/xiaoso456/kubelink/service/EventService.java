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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.xiaoso456.kubelink.constant.CommonConstant.ALL_NAMESPACE;
import static io.github.xiaoso456.kubelink.constant.CommonConstant.FIRST_CONTAINER;

@Service
public class EventService {

    @Autowired
    ConfigManagementService configManagementService;



    public List<CoreV1Event> listEvents(String namespace,String kind,String name){
        ApiClient apiClient = configManagementService.getApiClient();

        CoreV1Api coreV1Api = new CoreV1Api();
        coreV1Api.setApiClient(apiClient);

        try {
            CoreV1EventList coreV1EventList = coreV1Api.listNamespacedEvent(namespace).execute();
            List<CoreV1Event> eventList = new ArrayList<>();
            for(CoreV1Event item:coreV1EventList.getItems()){
                if(!Objects.equals(item.getInvolvedObject().getKind(),kind)){
                    continue;
                }
                if(!Objects.equals(item.getInvolvedObject().getName(),name)){
                    continue;
                }
                eventList.add(item);
            }
            return eventList;

        } catch (ApiException e) {
            throw new LinkRuntimeException(e);
        }
    }



}
