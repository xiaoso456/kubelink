package io.github.xiaoso456.kubelink.shell;


import io.github.xiaoso456.kubelink.domain.SyncInfo;
import io.github.xiaoso456.kubelink.service.SyncManagementService;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Command(command = "sync")
@Slf4j
public class SyncCommands {

    @Autowired
    SyncManagementService syncManagementService;


    @Command(command = "list")
    public String syncList() throws IOException, ApiException {
        StringBuilder sb = new StringBuilder();
        for (SyncInfo syncInfo : syncManagementService.listSyncInfo()) {
            sb.append(syncInfo.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


    @Command(command = "add")
    public void syncAdd() throws IOException, ApiException {
        // todo
    }

    @Command(command = "all")
    public void sync() {
        List<SyncInfo> syncInfos = syncManagementService.listSyncInfo();
        for (SyncInfo syncInfo : syncInfos) {
            syncManagementService.sync(syncInfo);
        }
    }

    @Command(command = "")
    public void syncById(@Option(required = true) String id) {
        SyncInfo syncInfo = syncManagementService.getSyncInfo(id);
        syncManagementService.sync(syncInfo);
    }
}
