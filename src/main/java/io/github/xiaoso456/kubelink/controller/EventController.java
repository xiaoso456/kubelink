package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.github.xiaoso456.kubelink.service.EventService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.CoreV1Event;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping("{namespace}/event/{kind}/{name}")
    public String listDeployment(@PathVariable String namespace, @PathVariable String kind,@PathVariable String name){
        List<CoreV1Event> v1EventList = eventService.listEvents(namespace, kind, name);
        return KubeApiUtils.toJsonString(v1EventList);
    }



    

}
