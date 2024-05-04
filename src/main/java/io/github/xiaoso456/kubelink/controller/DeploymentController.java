package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class DeploymentController {

    @Autowired
    DeploymentService deploymentService;

    @GetMapping("{namespace}/deployment/list")
    public JSON listDeployment(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1Deployment> v1Deployments = deploymentService.listDeployments(namespace);
        if(StrUtil.isBlank(search)){
            return JSONUtil.parse(v1Deployments);
        }else{
            v1Deployments = v1Deployments.stream().filter(v1Deployment -> v1Deployment.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return JSONUtil.parse(v1Deployments);
        }
    }

    @GetMapping("{namespace}/deployment/{deployment}")
    public JSON getDeployment(@PathVariable String namespace, @PathVariable String deployment){
        V1Deployment v1Deployment = deploymentService.getDeployment(namespace, deployment);
        return JSONUtil.parse(v1Deployment);

    }

    @GetMapping("{namespace}/deployment/{deployment}/pod/list")
    public JSON getDeploymentPods(@PathVariable String namespace, @PathVariable String deployment){
        return JSONUtil.parse(deploymentService.getDeploymentPods(namespace, deployment));
    }


    @PutMapping ("{namespace}/deployment/{deployment}/container/{container}/suspend")
    public void suspendDeployment(@PathVariable String namespace,@PathVariable String deployment,@PathVariable String container){

        deploymentService.suspendDeployment(namespace,deployment,container);

    }


    

}
