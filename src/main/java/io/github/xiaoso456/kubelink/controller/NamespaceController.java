package io.github.xiaoso456.kubelink.controller;

import io.github.xiaoso456.kubelink.service.NamespaceService;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class NamespaceController {

    @Autowired
    NamespaceService namespaceService;

    @GetMapping
    public List<V1Namespace> listNamespace(){
        return namespaceService.listNamespace();
    }

    @GetMapping("{namespace}/pods")
    public List<String> listPod(@PathVariable String namespace){
        List<V1Pod> v1Pods = namespaceService.listPods(namespace);
        return v1Pods.stream().map(v1Pod -> v1Pod.getMetadata().getName()).collect(Collectors.toList());
    }

    @GetMapping("{namespace}/pods/{pod}/containers")
    public List<String> listPodContainer(@PathVariable String namespace,@PathVariable String pod){
        List<V1Container> v1Containers = namespaceService.listPodContainers(namespace, pod);
        return v1Containers.stream().map(v1Container -> v1Container.getName()).collect(Collectors.toList());
    }

}
