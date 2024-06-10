package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.service.SecretService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class SecretController {

    @Autowired
    SecretService secretService;

    @GetMapping("{namespace}/secret/list")
    public String listSecret(@PathVariable String namespace, @RequestParam(required = false) String search){
        List<V1Secret> v1Secrets = secretService.listSecrets(namespace);
        if(StrUtil.isBlank(search)){
            return KubeApiUtils.toJsonString(v1Secrets);
        }else{
            v1Secrets = v1Secrets.stream().filter(v1Secret -> v1Secret.getMetadata().getName().contains(search)).collect(Collectors.toList());
            return KubeApiUtils.toJsonString(v1Secrets);
        }
    }

    @GetMapping("{namespace}/secret/{secret}")
    public String getSecret(@PathVariable String namespace, @PathVariable String secret){
        V1Secret v1Secret = secretService.getSecret(namespace, secret);
        return v1Secret.toJson();

    }

    @DeleteMapping("{namespace}/secret/{secret}")
    public String deleteSecret(@PathVariable String namespace, @PathVariable String secret){
        V1Status v1Status = secretService.deleteSecret(namespace, secret);
        return v1Status.toJson();

    }



    @PutMapping("{namespace}/secret/{secret}")
    public String updateSecret(@PathVariable String namespace,
                                    @PathVariable String secret,
                                    @RequestBody V1Secret v1Secret){

        V1Secret v1SecretNew = secretService.updateSecret(namespace, secret,v1Secret);
        return v1SecretNew.toJson();

    }

    @GetMapping("{namespace}/secret/{secret}/yaml")
    public String getSecretYaml(@PathVariable String namespace, @PathVariable String secret){
        return secretService.getSecretYaml(namespace, secret);

    }

    @PutMapping("{namespace}/secret/{secret}/yaml")
    public void updateSecretYaml(@PathVariable String namespace, @PathVariable String secret,
                                    @RequestBody String yaml){
        secretService.updateSecretYaml(namespace, secret,yaml);

    }




    

}
