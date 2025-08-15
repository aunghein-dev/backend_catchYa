package com.catch_ya_group.catch_ya.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Use an in-memory message broker
        config.enableSimpleBroker("/topic", "/user");
        // This prefix is for the application's message handling
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint.
        // It's crucial not to use .withSockJS() here to ensure compatibility
        // with both the provided HTML client and Postman.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Allowing all origins for testing
    }
}
