package io.github.xiaoso456.kubelink.controller;


import io.github.xiaoso456.kubelink.service.PodService;
import jakarta.websocket.*;
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
      log.info("message:{}",message);
    }

    @OnOpen
    public void openOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        log.info("session:{}",session);
    }


    @OnClose
    public void onClose(CloseReason closeReason){
        log.info("[websocket] 连接断开：id={}，reason={}", this.session.getId(),closeReason);
    }

    // 连接异常
    @OnError
    public void onError(Throwable throwable) throws IOException {

        log.info("[websocket] 连接异常：id={}，throwable={}", this.session.getId(), throwable.getMessage());

        // 关闭连接。状态码为 UNEXPECTED_CONDITION（意料之外的异常）
        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }
}
