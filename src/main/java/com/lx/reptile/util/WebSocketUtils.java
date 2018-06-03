package com.lx.reptile.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketUtils {
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void rt(String msg) {
        messagingTemplate.convertAndSend("/topic/notice", msg);
    }
}
