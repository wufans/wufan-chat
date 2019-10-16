package com.wufan.chat.wfchattestclient.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

/**
 * @Author: wufan
 * @Date: 2019/7/7 19:21
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer{

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //配置前端endpoint
        registry.addEndpoint("/socket")
                .setAllowedOrigins("*")
                .withSockJS()
                .setStreamBytesLimit(1024000)
                .setHttpMessageCacheSize(1024000);
//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        container.setDefaultMaxBinaryMessageBufferSize(102400);
//        container.setDefaultMaxTextMessageBufferSize(102400);
//        WebSocketClient transport = new StandardWebSocketClient(container);
//        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 后台服务推送前缀
        registry.enableSimpleBroker("/topic");
        // 客户端推送前缀，默认/user
//        registry.setApplicationDestinationPrefixes("/app");
    }

    //为了传输图片，需要配置最大的message size
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000)
                .setSendBufferSizeLimit(1024000)
                // max message size 2GB (2048 bytes) : default is 64KB
                .setMessageSizeLimit(2 * 1024 * 1024);
    }
}
