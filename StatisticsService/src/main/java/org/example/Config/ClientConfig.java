package org.example.Config;

import org.example.Client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Autowired
    private ApplicationConfig config;

    @Bean
    public GitHubClient getGitHubClient(@Value("${app.base-url-github}") String baseUrl) {

        int bufferSize = 1048576;

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(bufferSize))
                .build();

        WebClient webClient = WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .defaultHeader("Authorization", "token " + config.gitHubToken())
                .baseUrl(baseUrl)
                .build();
        return new GitHubClient(webClient);
    }
}
