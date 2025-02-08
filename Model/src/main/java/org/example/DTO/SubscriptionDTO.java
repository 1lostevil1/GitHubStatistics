package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionDTO(
        @JsonProperty("username") String username,
        @JsonProperty("url") String url
) {
}
