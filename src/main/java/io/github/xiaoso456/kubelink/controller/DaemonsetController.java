package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.github.xiaoso456.kubelink.service.DaemonsetService;
import io.github.xiaoso456.kubelink.service.StatefulsetService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class DaemonsetController {

    @Autowired
    DaemonsetService daemonsetService;

    @GetMapping("{namespace}/daemonset/list")
    public String listStatefulSets(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1DaemonSet> v1DaemonSets = daemonsetService.listDaemonsets(namespace);
        if(StrUtil.isBlank(search)){
            return KubeApiUtils.toJsonString(v1DaemonSets);
        }else{
            v1DaemonSets = v1DaemonSets.stream().filter(v1Deployment -> v1Deployment.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return KubeApiUtils.toJsonString(v1DaemonSets);
        }
    }

    @GetMapping("{namespace}/daemonset/{daemonset}")
    public String getDaemonset(@PathVariable String namespace, @PathVariable String daemonset){
        V1DaemonSet v1DaemonSet = daemonsetService.getDaemonsetset(namespace, daemonset);
        return v1DaemonSet.toJson();

    }

    @GetMapping("{namespace}/daemonset/{daemonset}/pod/list")
    public String getDaemonsetPods(@PathVariable String namespace, @PathVariable String daemonset){
        return KubeApiUtils.toJsonString(daemonsetService.getDaemonsetPods(namespace, daemonset));
    }

    @GetMapping("{namespace}/daemonset/{daemonset}/service/list")
    public String getDaemonsetServices(@PathVariable String namespace, @PathVariable String daemonset){
        return KubeApiUtils.toJsonString(daemonsetService.getDaemonsetServices(namespace, daemonset));

    }

    @PutMapping ("{namespace}/daemonset/{daemonset}/container/{container}/suspend")
    public void suspendDaemonset(@PathVariable String namespace,@PathVariable String daemonset,@PathVariable String container){
        daemonsetService.suspendsDaemonset(namespace,daemonset,container);
    }

}
