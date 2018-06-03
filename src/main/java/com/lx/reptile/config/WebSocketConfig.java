package com.lx.reptile.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    /**
     * 客户端与服务器端建立连接的点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/websocket").withSockJS();
    }

    /**
     * 配置客户端发送信息的路径的前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //客户端接收路径
        registry.enableSimpleBroker("/topic");
        //客户端发送路径
        registry.setApplicationDestinationPrefixes("/app");
    }


}
