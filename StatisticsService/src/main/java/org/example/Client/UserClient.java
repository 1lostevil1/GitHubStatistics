package org.example.Client;

import org.example.Request.Github.UpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;


public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void sendUpdate(UpdateRequest updateRequest) {
        webClient.post()
                .uri("/api/branches/updates")
                .body(Mono.just(updateRequest), UpdateRequest.class)
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
