package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.ConfigMapService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class ConfigMapController {

    @Autowired
    ConfigMapService configMapService;

    @GetMapping("{namespace}/configmap/list")
    public String listService(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1ConfigMap> v1ConfigMaps = configMapService.listConfigMaps(namespace);
        if(StrUtil.isBlank(search)){
            return KubeApiUtils.toJsonString(v1ConfigMaps);
        }else{
            v1ConfigMaps = v1ConfigMaps.stream().filter(v1ConfigMap -> v1ConfigMap.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return KubeApiUtils.toJsonString(v1ConfigMaps);
        }
    }

    @GetMapping("{namespace}/configmap/{configmap}")
    public String getConfigmap(@PathVariable String namespace, @PathVariable String configmap){
        V1ConfigMap v1ConfigMap = configMapService.getConfigMap(namespace, configmap);
        return v1ConfigMap.toJson();

    }

    @DeleteMapping("{namespace}/configmap/{configmap}")
    public String deleteConfigmap(@PathVariable String namespace, @PathVariable String configmap){
        V1Status v1Status = configMapService.deleteConfigMap(namespace, configmap);
        return v1Status.toJson();

    }

    @PostMapping("{namespace}/configmap")
    public String createConfigMap(@PathVariable String namespace,
                                    @RequestBody V1ConfigMap v1ConfigMap){

        V1ConfigMap v1ConfigMapNew = configMapService.createConfigMap(namespace,v1ConfigMap);
        return v1ConfigMapNew.toJson();

    }



    @PutMapping("{namespace}/configmap/{configmap}")
    public String updateConfigMap(@PathVariable String namespace,
                                    @PathVariable String configmap,
                                    @RequestBody V1ConfigMap v1ConfigMap){

        V1ConfigMap v1ConfigMapNew = configMapService.updateConfigMap(namespace, configmap,v1ConfigMap);
        return v1ConfigMapNew.toJson();

    }

    @GetMapping("{namespace}/configmap/{configmap}/yaml")
    public String getConfigmapYaml(@PathVariable String namespace, @PathVariable String configmap){
        return configMapService.getConfigmapYaml(namespace, configmap);

    }

    @PutMapping("{namespace}/configmap/{configmap}/yaml")
    public void updateConfigmapYaml(@PathVariable String namespace, @PathVariable String configmap,
                                    @RequestBody String yaml){
        configMapService.updateConfigmapYaml(namespace, configmap,yaml);

    }






    

}
