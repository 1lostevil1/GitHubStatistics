package org.example.Client;

import lombok.AllArgsConstructor;
import org.example.Request.Github.UpdateRequest;
import org.example.Request.User.SubscriptionRequest;
import org.example.Response.User.SubscriptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class StatisticsClient {

    private final WebClient webClient;

    public void sendSubscription(SubscriptionRequest subscriptionRequest) {
        webClient.post()
                .uri("/stat/recentlyAdded")
                .body(Mono.just(subscriptionRequest), SubscriptionRequest.class)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("not found"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера"
                        ))
                ).bodyToMono(Void.class).block();
    }
}
