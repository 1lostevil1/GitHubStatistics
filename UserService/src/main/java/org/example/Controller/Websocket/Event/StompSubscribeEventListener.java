package org.example.Controller.Websocket.Event;

import org.example.Service.SubscriptionService;
import org.example.Utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Component
public class StompSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private  JwtTokenUtils jwtTokenUtils;



    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        // Получаем StompHeaderAccessor из события подписки
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Извлекаем информацию о подписке
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();

        // Извлекаем токен из заголовков (если он передан)
        List<String> authorizationHeader = headerAccessor.getNativeHeader("Authorization");
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            String token = authorizationHeader.get(0);  // Обычно это первый элемент, если токен передан

            // Проверяем, что токен начинается с "Bearer "
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);  // Убираем "Bearer " и получаем сам токен

                // Извлекаем username из токена
                String username = jwtTokenUtils.getUsername(jwtToken);

                if (username != null) {


                    // Логирование или дополнительные действия
                    System.out.println("User [" + username + "] subscribed to " + destination
                            + " (subscriptionId=" + subscriptionId + ", sessionId=" + sessionId + ")");
                } else {
                    System.out.println("No username found in token. Subscription rejected.");
                }
            } else {
                System.out.println("Invalid token format (missing 'Bearer').");
            }
        } else {
            System.out.println("Authorization token is missing.");
        }
    }


}