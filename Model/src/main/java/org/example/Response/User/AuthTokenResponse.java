package org.example.Response.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenResponse(
        @JsonProperty("token") String token
) {
}
