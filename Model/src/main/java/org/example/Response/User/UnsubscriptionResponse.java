package org.example.Response.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UnsubscriptionResponse(
        @JsonProperty("username") String username,
        @JsonProperty("url") String url
) {
}
