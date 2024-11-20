package org.example.Config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
public record ApplicationConfig(
        @NotNull String baseUrlStatistics,
        @NotNull String baseUrlGitHub,
        @NotNull String gitHubToken,
        @Bean Scheduler scheduler) {



    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}

