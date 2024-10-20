package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.domain.helm.Helm;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.github.xiaoso456.kubelink.service.SecretService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class HelmController {

    @Autowired
    SecretService secretService;



    @GetMapping("{namespace}/helm/list")
    public String listHelm(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1Secret> v1Secrets = secretService.listSecrets(namespace);
        List<Helm> v1SecretList = v1Secrets.stream().filter(
                v1Secret -> Objects.equals(v1Secret.getType(), "helm.sh/release.v1")
        ).map(secret -> Helm.fromSecret(secret))
                .toList();
        return "";
    }



    

}
