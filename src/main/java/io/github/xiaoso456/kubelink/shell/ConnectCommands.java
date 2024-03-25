package io.github.xiaoso456.kubelink.shell;

import io.github.xiaoso456.kubelink.config.KubeConfig;
import io.github.xiaoso456.kubelink.service.ConfigManagementService;
import io.github.xiaoso456.kubelink.utils.KubeApiUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.VersionApi;
import io.kubernetes.client.openapi.models.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(command = "connect")
public class ConnectCommands {


    @Autowired
    private ConfigManagementService configManagementService;

    @Command(command = "")
    public VersionInfo connect() throws IOException, ApiException {
        ApiClient apiClient = configManagementService.getApiClient();
        VersionApi versionApi = new VersionApi();
        versionApi.setApiClient(apiClient);
        return versionApi.getCode().execute();
    }
}
