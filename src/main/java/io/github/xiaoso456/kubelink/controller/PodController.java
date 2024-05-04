package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.github.xiaoso456.kubelink.service.PodService;
import io.kubernetes.client.openapi.models.V1Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class PodController {

    @Autowired
    PodService podService;

    @GetMapping("{namespace}/pod/{pod}/container/{container}/logs")
    public String getPodLogs(@PathVariable String namespace,
                             @PathVariable String pod,
                             @PathVariable String container,
                             @RequestParam(required = false,defaultValue = "false") boolean previous,
                             @RequestParam(required = false,defaultValue = "1000") int tailLines){
        return podService.getPodLogs(namespace, pod, container,previous,tailLines);
    }

    

}
