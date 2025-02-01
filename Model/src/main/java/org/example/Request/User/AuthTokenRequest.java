package org.example.Request.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenRequest(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {
}
