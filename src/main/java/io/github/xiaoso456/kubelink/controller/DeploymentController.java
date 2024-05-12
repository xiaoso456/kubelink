package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Status;
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
    @DeleteMapping("{namespace}/deployment/{deployment}")
    public String deleteDeployment(@PathVariable String namespace, @PathVariable String deployment){
        V1Status v1Status = deploymentService.deleteDeployment(namespace, deployment);
        return v1Status.toJson();

    }

    @GetMapping("{namespace}/deployment/{deployment}/yaml")
    public String getDeploymentYaml(@PathVariable String namespace, @PathVariable String deployment){
        return deploymentService.getDeploymentYaml(namespace, deployment);

    }

    @PutMapping("{namespace}/deployment/{deployment}/yaml")
    public void updateDeploymentYaml(@PathVariable String namespace, @PathVariable String deployment,
                                       @RequestBody String yaml){
        deploymentService.updateDeploymentYaml(namespace, deployment,yaml);

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
