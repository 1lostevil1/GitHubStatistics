package org.example.Config.ClientConfig;

import org.example.Client.StatisticsClient;
import org.example.Config.AppConfig.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StatisticsClientConfig {

    @Autowired
    private ApplicationConfig config;

    @Bean
    public StatisticsClient getUserClient(){

        WebClient webClient = WebClient.builder()
                .baseUrl(config.baseUrlStat())
                .build();

        return new StatisticsClient(webClient);
    }
}
