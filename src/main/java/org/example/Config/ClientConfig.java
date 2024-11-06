package org.example.Config;

import org.example.Client.GitHubClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public GitHubClient getGitHubClient(@Value("${app.base-url-github}") String baseUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        return new GitHubClient(webClient);
    }
}
