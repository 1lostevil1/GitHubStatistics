package org.example.Client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Request.Github.UpdateRequest;
import org.example.Request.User.SubscriptionRequest;
import org.example.Response.User.SubscriptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class StatisticsClient {

    private final WebClient webClient;

    @Async
    public void sendSubscription(SubscriptionRequest subscriptionRequest) {

        log.info(subscriptionRequest.toString());

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
                )
                .bodyToMono(void.class)
                .subscribe(
                        null,
                        error -> log.error("Ошибка при отправке запроса: ", error) // Обработка ошибок
                );
    }
}
