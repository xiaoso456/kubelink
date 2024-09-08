package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.ServiceService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class ServiceController {

    @Autowired
    ServiceService serviceService;

    @GetMapping("{namespace}/service/list")
    public String listService(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1Service> v1Services = serviceService.listServices(namespace);
        if(StrUtil.isBlank(search)){
            return KubeApiUtils.toJsonString(v1Services);
        }else{
            v1Services = v1Services.stream().filter(v1Service -> v1Service.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return KubeApiUtils.toJsonString(v1Services);
        }
    }

    @GetMapping("{namespace}/service/{service}")
    public String getService(@PathVariable String namespace, @PathVariable String service){
        V1Service v1Service = serviceService.getService(namespace, service);
        return v1Service.toJson();

    }

    @GetMapping("{namespace}/service/{service}/yaml")
    public String getServiceYaml(@PathVariable String namespace, @PathVariable String service){
        return serviceService.getServiceYaml(namespace, service);
    }

    @PutMapping("{namespace}/service/{service}/yaml")
    public void updateServiceYaml(@PathVariable String namespace, @PathVariable String service,
                                     @RequestBody String yaml){
        serviceService.updateServiceYaml(namespace, service, yaml);

    }


    @DeleteMapping("{namespace}/service/{service}")
    public String deleteService(@PathVariable String namespace, @PathVariable String service){
        V1Service v1Service = serviceService.deleteService(namespace, service);
        return v1Service.toJson();

    }



    @PutMapping("{namespace}/service/{service}")
    public String updateService(@PathVariable String namespace,
                                    @PathVariable String service,
                                    @RequestBody V1Service v1Service){

        V1Service v1ServiceNew = serviceService.updateService(namespace, service,v1Service);
        return v1ServiceNew.toJson();

    }




    

}
