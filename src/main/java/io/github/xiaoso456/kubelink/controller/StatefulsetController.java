package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.github.xiaoso456.kubelink.service.StatefulsetService;
import io.kubernetes.client.openapi.models.V1Deployment;
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
    public JSON listStatefulSets(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1StatefulSet> v1StatefulSets = statefulsetService.listStatefulsets(namespace);
        if(StrUtil.isBlank(search)){
            return JSONUtil.parse(v1StatefulSets);
        }else{
            v1StatefulSets = v1StatefulSets.stream().filter(v1Deployment -> v1Deployment.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return JSONUtil.parse(v1StatefulSets);
        }
    }

    @GetMapping("{namespace}/statefulset/{statefulset}")
    public JSON getStatefulset(@PathVariable String namespace, @PathVariable String statefulset){
        V1StatefulSet v1StatefulSet = statefulsetService.getStatefulset(namespace, statefulset);
        return JSONUtil.parse(v1StatefulSet);

    }

    @GetMapping("{namespace}/statefulset/{statefulset}/pod/list")
    public JSON getStatefulsetPods(@PathVariable String namespace, @PathVariable String statefulset){
        return JSONUtil.parse(statefulsetService.getStatefulsetPods(namespace, statefulset));
    }


    @PutMapping ("{namespace}/statefulset/{statefulset}/container/{container}/suspend")
    public void suspendDeployment(@PathVariable String namespace,@PathVariable String statefulset,@PathVariable String container){

        statefulsetService.suspendsSatefulset(namespace,statefulset,container);

    }

}
