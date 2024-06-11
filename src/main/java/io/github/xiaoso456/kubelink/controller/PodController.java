package io.github.xiaoso456.kubelink.controller;


import io.github.xiaoso456.kubelink.service.PodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("{namespace}/pod/{pod}")
    public void deletePod(@PathVariable String namespace, @PathVariable String pod) {
        podService.deletePod(namespace, pod);
    }

    

}
