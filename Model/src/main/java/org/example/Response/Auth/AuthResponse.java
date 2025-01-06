package org.example.Response.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("username") String username,
        @JsonProperty("passwordHash") int passwordHash
) {
}
