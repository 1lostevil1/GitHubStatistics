package org.example.Request.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UnsubscriptionRequest(
        @JsonProperty("url") String url) {
}
