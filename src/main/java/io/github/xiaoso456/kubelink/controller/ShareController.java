package io.github.xiaoso456.kubelink.controller;


import io.github.xiaoso456.kubelink.domain.share.SharedDatum;
import io.github.xiaoso456.kubelink.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @GetMapping("/cluster-config/export")
    public Object exportClusterConfig(@RequestParam(required = false) List<Long> ids){
        return shareService.exportClusterConfig(ids);
    }

    @GetMapping("/sync-config/export")
    public Object exportSyncConfig(@RequestParam(required = false) List<Long> ids){
        return shareService.exportSyncConfig(ids);
    }

    @GetMapping("/text-template/export")
    public Object exportTemplate(@RequestParam(required = false) List<Long> ids){
        return shareService.exportTextTemplate(ids);
    }

    @PostMapping("/common/import")
    public void importData(@RequestBody SharedDatum sharedDatum){
        shareService.importSharedDatum(sharedDatum);
    }
}
