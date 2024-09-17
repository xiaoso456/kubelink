package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.DaemonsetService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Status;
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

    @DeleteMapping("{namespace}/daemonset/{daemonset}")
    public String deleteDaemonset(@PathVariable String namespace, @PathVariable String daemonset){
        V1Status v1Status = daemonsetService.deleteDaemonset(namespace, daemonset);
        return v1Status.toJson();

    }

    @PutMapping("{namespace}/daemonset/{daemonset}")
    public String updateDaemonset(@PathVariable String namespace,
                                   @PathVariable String daemonset,
                                   @RequestBody V1DaemonSet v1DaemonSet){

        V1DaemonSet daemonsetNew = daemonsetService.updateDaemonset(namespace, daemonset, v1DaemonSet);
        return daemonsetNew.toJson();

    }

    @GetMapping("{namespace}/daemonset/{daemonset}/pod/list")
    public String getDaemonsetPods(@PathVariable String namespace, @PathVariable String daemonset){
        return KubeApiUtils.toJsonString(daemonsetService.getDaemonsetPods(namespace, daemonset));
    }

    @GetMapping("{namespace}/daemonset/{daemonset}/service/list")
    public String getDaemonsetServices(@PathVariable String namespace, @PathVariable String daemonset){
        return KubeApiUtils.toJsonString(daemonsetService.getDaemonsetServices(namespace, daemonset));

    }
    @GetMapping("{namespace}/daemonset/{daemonset}/yaml")
    public String getDaemonsetYaml(@PathVariable String namespace, @PathVariable String daemonset){
        return daemonsetService.getDaemonsetYaml(namespace, daemonset);

    }

    @PutMapping("{namespace}/daemonset/{daemonset}/yaml")
    public void updateDaemonsetYaml(@PathVariable String namespace, @PathVariable String daemonset,
                                     @RequestBody String yaml){
        daemonsetService.updateDaemonsetYaml(namespace, daemonset,yaml);

    }
    @PutMapping ("{namespace}/daemonset/{daemonset}/container/{container}/suspend")
    public void suspendDaemonset(@PathVariable String namespace,@PathVariable String daemonset,@PathVariable String container){
        daemonsetService.suspendsDaemonset(namespace,daemonset,container);
    }

}
