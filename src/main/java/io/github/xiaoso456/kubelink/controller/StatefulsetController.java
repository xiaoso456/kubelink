package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;

import io.github.xiaoso456.kubelink.service.StatefulsetService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class StatefulsetController {

    @Autowired
    StatefulsetService statefulsetService;

    @GetMapping("{namespace}/statefulset/list")
    public String listStatefulSets(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1StatefulSet> v1StatefulSets = statefulsetService.listStatefulsets(namespace);
        if(StrUtil.isBlank(search)){
            return KubeApiUtils.toJsonString(v1StatefulSets);
        }else{
            v1StatefulSets = v1StatefulSets.stream().filter(v1Deployment -> v1Deployment.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return KubeApiUtils.toJsonString(v1StatefulSets);
        }
    }

    @GetMapping("{namespace}/statefulset/{statefulset}")
    public String getStatefulset(@PathVariable String namespace, @PathVariable String statefulset){
        V1StatefulSet v1StatefulSet = statefulsetService.getStatefulset(namespace, statefulset);
        return v1StatefulSet.toJson();

    }

    @GetMapping("{namespace}/statefulset/{statefulset}/pod/list")
    public String getStatefulsetPods(@PathVariable String namespace, @PathVariable String statefulset){
        return KubeApiUtils.toJsonString(statefulsetService.getStatefulsetPods(namespace, statefulset));

    }

    @GetMapping("{namespace}/statefulset/{statefulset}/service/list")
    public String getDeploymentServices(@PathVariable String namespace, @PathVariable String statefulset){
        return KubeApiUtils.toJsonString(statefulsetService.getStatefulsetServices(namespace, statefulset));

    }


    @PutMapping ("{namespace}/statefulset/{statefulset}/container/{container}/suspend")
    public void suspendDeployment(@PathVariable String namespace,@PathVariable String statefulset,@PathVariable String container){

        statefulsetService.suspendsSatefulset(namespace,statefulset,container);

    }

}
