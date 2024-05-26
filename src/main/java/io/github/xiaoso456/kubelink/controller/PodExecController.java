package io.github.xiaoso456.kubelink.controller;


import io.github.xiaoso456.kubelink.service.PodService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@ServerEndpoint("/ws/namespace/{namespace}/pod/{pod}/container/{container}/exec")
@Slf4j
public class PodExecController {

    @Autowired
    PodService podService;


    private Session session;

    @OnMessage
    public void onMessage(String message) {
      // log.info("message:{}",message);
    }

    @OnOpen
    public void openOpen(Session session, EndpointConfig endpointConfig,
                         @PathParam("namespace") String namespace,
                         @PathParam("pod") String pod,
                         @PathParam("container") String container) {
        this.session = session;
        log.info("[websocket] create new session on namespace [{}] pod [{}] container [{}]",namespace,pod,container);
        // create k8s websocket,and write it
    }


    @OnClose
    public void onClose(CloseReason closeReason){
        log.info("[websocket] connection close id [{}] reason=[{}]", this.session.getId(),closeReason);
    }

    @OnError
    public void onError(Throwable throwable) throws IOException {

        log.info("[websocket] connection error id [{}] throwable [{}]", this.session.getId(), throwable.getMessage());

        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }
}
