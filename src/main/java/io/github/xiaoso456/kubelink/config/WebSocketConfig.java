package io.github.xiaoso456.kubelink.config;

import io.github.xiaoso456.kubelink.controller.PodExecController;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();
        serverEndpointExporter.setAnnotatedEndpointClasses(PodExecController.class);
        return serverEndpointExporter;
    }

    @Bean("websocketExecutorService")
    public ExecutorService websocketExecutorService(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        return executorService;
    }


}
