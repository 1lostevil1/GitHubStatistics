package org.example.Client;

import org.example.DTO.UserDTO;
import org.example.Request.User.CurrentUpdateRequest;
import org.example.Request.User.UpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void sendUpdate(UpdateRequest updateRequest) {
        webClient.post()
                .uri("/api/updates")
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

    public void sendCurrentUpdate(CurrentUpdateRequest currentUpdateRequest) {
        webClient.post()
                .uri("/api/currentUpdates")
                .body(Mono.just(currentUpdateRequest), UpdateRequest.class)
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
