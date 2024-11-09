package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.domain.helm.Helm;
import io.github.xiaoso456.kubelink.service.DeploymentService;
import io.github.xiaoso456.kubelink.service.SecretService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Status;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/namespace")
public class HelmController {

    @Autowired
    SecretService secretService;

    @Autowired
    private HttpServletResponse httpServletResponse;



    @GetMapping("{namespace}/helm/list")
    public List<Helm> listHelm(@PathVariable String namespace, @RequestParam(required = false) String search) throws IOException {
        List<V1Secret> v1Secrets = secretService.listSecrets(namespace);
        List<Helm> v1SecretList = new ArrayList<>();
        for (V1Secret v1Secret : v1Secrets) {
            if (Objects.equals(v1Secret.getType(), "helm.sh/release.v1")) {
                Helm helm = Helm.fromSecretWithoutRelease(v1Secret);
                v1SecretList.add(helm);
            }
        }
        return v1SecretList;
    }

    @GetMapping("{namespace}/helm/{releaseName}/export")
    public void exportHelm(@PathVariable String namespace, @PathVariable String releaseName) throws IOException {
        List<V1Secret> v1Secrets = secretService.listSecrets(namespace);
        for (V1Secret v1Secret : v1Secrets) {
            boolean exportedRelease = Objects.equals(v1Secret.getType(), "helm.sh/release.v1") &&
                    Objects.equals(releaseName,v1Secret.getMetadata().getLabels().getOrDefault("name",""));
            if (exportedRelease) {
                Helm helm = Helm.fromSecret(v1Secret);
                String exportName = helm.getNamespace() + "_" + helm.getName() + "_" + helm.getVersion();
                httpServletResponse.setContentType("application/zip");
                httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + exportName + ".zip\"");
                helm.exportZip(httpServletResponse.getOutputStream());
            }
        }

    }




    // @GetMapping("{namespace}/helm/list/test")
    // public List<Helm> listHelmTest(@PathVariable String namespace, @RequestParam(required = false) String search) throws IOException {
    //     List<V1Secret> v1Secrets = secretService.listSecrets(namespace);
    //     List<Helm> helmList = new ArrayList<>();
    //     for (V1Secret v1Secret : v1Secrets) {
    //         if (Objects.equals(v1Secret.getType(), "helm.sh/release.v1")) {
    //             Helm helm = Helm.fromSecret(v1Secret);
    //             helmList.add(helm);
    //         }
    //     }
    //     return helmList;
    // }


    

}
