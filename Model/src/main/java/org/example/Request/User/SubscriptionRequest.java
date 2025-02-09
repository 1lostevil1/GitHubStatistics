package org.example.Request.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionRequest(
        @JsonProperty("url") String url

) {
}
