package org.example.Response.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionResponse(
        @JsonProperty("username") String username,
        @JsonProperty("url") String url
) {
}
