package io.github.xiaoso456.kubelink.controller;


import cn.hutool.core.util.ByteUtil;
import io.github.xiaoso456.kubelink.service.ClusterConfigService;
import io.github.xiaoso456.kubelink.service.ConfigManagementService;
import io.github.xiaoso456.kubelink.service.PodService;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Streams;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@ServerEndpoint("/ws/namespace/{namespace}/pod/{pod}/container/{container}/exec")
@Slf4j
@Controller
public class PodExecController {


    private static PodService podService;

    private static ClusterConfigService clusterConfigService;

    private static ExecutorService websocketExecutorService;

    private static ConfigManagementService configManagementService;

    private static int DEFAULT_BUFFER_SIZE = 4096;


    private ConcurrentHashMap<String,Process> sessionIdProcessMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String,Session> sessionIdSessionMap = new ConcurrentHashMap<>();




    @Autowired
    private void setPodService(PodService podService) {
        PodExecController.podService = podService;
    }

    @Autowired
    private void setClusterConfigService(ClusterConfigService clusterConfigService) {
        PodExecController.clusterConfigService = clusterConfigService;
    }

    @Autowired
    private void setWebsocketExecutorService(ExecutorService websocketExecutorService) {
        PodExecController.websocketExecutorService = websocketExecutorService;
    }

    @Autowired
    public static void setConfigManagementService(ConfigManagementService configManagementService) {
        PodExecController.configManagementService = configManagementService;
    }

    @OnMessage
    public void onMessage(Session session,String message) throws IOException {
        sessionIdProcessMap.get(session.getId()).getOutputStream().write(message.getBytes());
        // process.getOutputStream().write(message.getBytes());


    }

    @OnOpen
    public void openOpen(Session session, EndpointConfig endpointConfig,
                         @PathParam("namespace") String namespace,
                         @PathParam("pod") String pod,
                         @PathParam("container") String container) throws IOException, ApiException {
        log.info("[websocket] create new session on namespace [{}] pod [{}] container [{}]",namespace,pod,container);
        sessionIdSessionMap.put(session.getId(),session);

        ApiClient apiClient = configManagementService.getApiClient();
        apiClient.setVerifyingSsl(false);
        Exec exec = new Exec(apiClient);
        Process process = exec.exec(namespace, pod, new String[]{"sh"}, container, true, true);

        sessionIdProcessMap.put(session.getId(),process);
        websocketExecutorService.execute(() -> {
            try {
                InputStream processInputStream = process.getInputStream();

                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = processInputStream.read(buffer)) != -1) {
                    session.getBasicRemote().sendText(new String(buffer,0,bytesRead));
                }
                process.waitFor();
                process.destroy();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });



    }


    @OnClose
    public void onClose(CloseReason closeReason,Session session){
        sessionIdProcessMap.get(session.getId()).destroy();
        sessionIdProcessMap.remove(session.getId());
        log.info("[websocket] connection close id [{}] reason=[{}]",session.getId(),closeReason);
    }

    @OnError
    public void onError(Throwable throwable,Session session) throws IOException {
        sessionIdProcessMap.get(session.getId()).destroy();
        sessionIdProcessMap.remove(session.getId());
        log.info("[websocket] connection error id [{}] throwable [{}]", session.getId(), throwable.getMessage());
        session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }
}
