package org.example.Request.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionRequest(
        @JsonProperty("username") String username,
        @JsonProperty("url") String url

) {
}
