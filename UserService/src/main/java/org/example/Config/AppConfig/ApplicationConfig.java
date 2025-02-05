package org.example.Config.AppConfig;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;



@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
public record ApplicationConfig( @NotNull String baseUrlStat ) {
}
