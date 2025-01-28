package org.example.Client;

import org.springframework.web.reactive.function.client.WebClient;

public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient webClient) {
        this.webClient = webClient;
    }


}
