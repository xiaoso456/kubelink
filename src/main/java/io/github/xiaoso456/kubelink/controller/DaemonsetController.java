package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.github.xiaoso456.kubelink.service.DaemonsetService;
import io.github.xiaoso456.kubelink.service.StatefulsetService;
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
    public JSON listStatefulSets(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1DaemonSet> v1DaemonSets = daemonsetService.listDaemonsets(namespace);
        if(StrUtil.isBlank(search)){
            return JSONUtil.parse(v1DaemonSets);
        }else{
            v1DaemonSets = v1DaemonSets.stream().filter(v1Deployment -> v1Deployment.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return JSONUtil.parse(v1DaemonSets);
        }
    }

    @GetMapping("{namespace}/daemonset/{daemonset}")
    public JSON getDaemonset(@PathVariable String namespace, @PathVariable String daemonset){
        V1DaemonSet v1DaemonSet = daemonsetService.getDaemonsetset(namespace, daemonset);
        return JSONUtil.parse(v1DaemonSet);

    }

    @GetMapping("{namespace}/daemonset/{daemonset}/pod/list")
    public JSON getDaemonsetPods(@PathVariable String namespace, @PathVariable String daemonset){
        return JSONUtil.parse(daemonsetService.getDaemonsetPods(namespace, daemonset));
    }

    @PutMapping ("{namespace}/daemonset/{daemonset}/container/{container}/suspend")
    public void suspendDaemonset(@PathVariable String namespace,@PathVariable String daemonset,@PathVariable String container){
        daemonsetService.suspendsDaemonset(namespace,daemonset,container);
    }

}
