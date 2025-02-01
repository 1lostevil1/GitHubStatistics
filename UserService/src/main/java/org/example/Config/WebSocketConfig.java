package org.example.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*Конфигурирует простой брокер сообщений в памяти с одним адресом с префиксом для отправки и получения сообщений.
    Адреса с префиксом /app предназначены для сообщений, обрабатываемых методами с аннотацией @MessageMapping*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем брокер для топиков, по которым мы будем слать сообщения клиентам
        config.enableSimpleBroker("/topic");
        // Префикс для сообщений, которые отправляет клиент (например, @MessageMapping("/somePath"))
        config.setApplicationDestinationPrefixes("/app");
    }


    /*регистрирует конечную точку STOMP /ws.
    Эта конечная точка будет использоваться клиентами для подключения к STOMP-серверу*/
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

}
