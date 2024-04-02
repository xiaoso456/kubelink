package io.github.xiaoso456.kubelink.controller;

import io.github.xiaoso456.kubelink.service.NamespaceService;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1PodTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<V1PodTemplate> listPod(@PathVariable String namespace){
        return namespaceService.listPods(namespace);
    }

}
