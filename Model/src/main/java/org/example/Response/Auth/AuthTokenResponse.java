package org.example.Response.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenResponse(
        @JsonProperty("token") String token
) {
}
