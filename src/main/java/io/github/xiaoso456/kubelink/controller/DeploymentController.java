package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Deployment;
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
    public String listDeployment(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1Deployment> v1Deployments = deploymentService.listDeployments(namespace);
        if(StrUtil.isBlank(search)){
            return KubeApiUtils.toJsonString(v1Deployments);
        }else{
            v1Deployments = v1Deployments.stream().filter(v1Deployment -> v1Deployment.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return KubeApiUtils.toJsonString(v1Deployments);
        }
    }

    @GetMapping("{namespace}/deployment/{deployment}")
    public String getDeployment(@PathVariable String namespace, @PathVariable String deployment){
        V1Deployment v1Deployment = deploymentService.getDeployment(namespace, deployment);
        return v1Deployment.toJson();

    }

    @GetMapping("{namespace}/deployment/{deployment}/pod/list")
    public String getDeploymentPods(@PathVariable String namespace, @PathVariable String deployment){
        return KubeApiUtils.toJsonString(deploymentService.getDeploymentPods(namespace, deployment));
    }

    @GetMapping("{namespace}/deployment/{deployment}/service/list")
    public String getDeploymentServices(@PathVariable String namespace, @PathVariable String deployment){
        return KubeApiUtils.toJsonString(deploymentService.getDeploymentServices(namespace, deployment));

    }


    @PutMapping ("{namespace}/deployment/{deployment}/container/{container}/suspend")
    public void suspendDeployment(@PathVariable String namespace,@PathVariable String deployment,@PathVariable String container){

        deploymentService.suspendDeployment(namespace,deployment,container);

    }


    

}
