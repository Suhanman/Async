//package com.example.hellomessagequeue.step3;
//
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config){
//        config.enableSimpleBroker("/topic"); // client 구독 경로
//        config.setApplicationDestinationPrefixes("/app"); //server 구독 경로
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry){
//        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS(); // Websocket 엔드포인트
//    }
//}
