package com.example.banking_project.config;

import com.example.banking_project.handler.AccountWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AccountWebSocketHandler accountWebSocketHandler;

    public WebSocketConfig(AccountWebSocketHandler accountWebSocketHandler) {
        this.accountWebSocketHandler = accountWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(accountWebSocketHandler, "/ws/accounts").setAllowedOrigins("*");
    }
}
