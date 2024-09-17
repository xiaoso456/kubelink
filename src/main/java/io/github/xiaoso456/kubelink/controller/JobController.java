package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.JobService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class JobController {

    @Autowired
    JobService jobService;

    @GetMapping("{namespace}/job/list")
    public String listJob(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1Job> v1Jobs = jobService.listJobs(namespace);
        if(StrUtil.isBlank(search)){
            return KubeApiUtils.toJsonString(v1Jobs);
        }else{
            v1Jobs = v1Jobs.stream().filter(v1Job -> v1Job.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return KubeApiUtils.toJsonString(v1Jobs);
        }
    }

    @GetMapping("{namespace}/job/{job}")
    public String getJob(@PathVariable String namespace, @PathVariable String job){
        V1Job v1Job = jobService.getJob(namespace, job);
        return v1Job.toJson();

    }

    @DeleteMapping("{namespace}/job/{job}")
    public String deleteJob(@PathVariable String namespace, @PathVariable String job){
        V1Status v1Status = jobService.deleteJob(namespace, job);
        return v1Status.toJson();

    }

    @PutMapping("{namespace}/job/{job}")
    public String updateJob(@PathVariable String namespace,
                                   @PathVariable String job,
                                   @RequestBody V1Job v1Job){

        V1Job jobNew = jobService.updateJob(namespace, job, v1Job);
        return jobNew.toJson();

    }


    @GetMapping("{namespace}/job/{job}/yaml")
    public String getJobYaml(@PathVariable String namespace, @PathVariable String job){
        return jobService.getJobYaml(namespace, job);

    }

    @PutMapping("{namespace}/job/{job}/yaml")
    public void updateJobYaml(@PathVariable String namespace, @PathVariable String job,
                                       @RequestBody String yaml){
        jobService.updateJobYaml(namespace, job,yaml);

    }

    @GetMapping("{namespace}/job/{job}/pod/list")
    public String getJobPods(@PathVariable String namespace, @PathVariable String job){
        return KubeApiUtils.toJsonString(jobService.getJobPods(namespace, job));
    }

    @GetMapping("{namespace}/job/{job}/service/list")
    public String getJobServices(@PathVariable String namespace, @PathVariable String job){
        return KubeApiUtils.toJsonString(jobService.getJobServices(namespace, job));

    }


    @PutMapping ("{namespace}/job/{job}/container/{container}/suspend")
    public void suspendJob(@PathVariable String namespace,@PathVariable String job,@PathVariable String container){

        throw new RuntimeException("not support");

    }


    

}
