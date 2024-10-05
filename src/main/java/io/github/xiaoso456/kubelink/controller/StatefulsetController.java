package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.StatefulsetService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1Status;
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

    @DeleteMapping("{namespace}/statefulset/{statefulset}")
    public String deleteStatefulset(@PathVariable String namespace, @PathVariable String statefulset){
        V1Status v1Status = statefulsetService.deleteStatefulset(namespace, statefulset);
        return v1Status.toJson();

    }

    @PutMapping("{namespace}/statefulset/{statefulset}")
    public String updateStatefulset(@PathVariable String namespace,
                                   @PathVariable String statefulset,
                                   @RequestBody V1StatefulSet v1StatefulSet){

        V1StatefulSet statefulsetNew = statefulsetService.updateStatefulset(namespace, statefulset, v1StatefulSet);
        return statefulsetNew.toJson();

    }

    @PostMapping("{namespace}/statefulset")
    public String createStatefulset(@PathVariable String namespace, @RequestBody V1StatefulSet v1StatefulSet){

        V1StatefulSet statefulset = statefulsetService.createStatefulset(namespace, v1StatefulSet);
        return statefulset.toJson();

    }

    @GetMapping("{namespace}/statefulset/{statefulset}/pod/list")
    public String getStatefulsetPods(@PathVariable String namespace, @PathVariable String statefulset){
        return KubeApiUtils.toJsonString(statefulsetService.getStatefulsetPods(namespace, statefulset));

    }

    @GetMapping("{namespace}/statefulset/{statefulset}/yaml")
    public String getDeploymentYaml(@PathVariable String namespace, @PathVariable String statefulset){
        return statefulsetService.getStatefulsetYaml(namespace, statefulset);

    }

    @PutMapping("{namespace}/statefulset/{statefulset}/yaml")
    public void updateDeploymentYaml(@PathVariable String namespace, @PathVariable String statefulset,
                                     @RequestBody String yaml){
        statefulsetService.updateStatefulsetYaml(namespace, statefulset,yaml);

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
