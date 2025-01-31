package org.example.Response.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegistrationResponse(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email
) {
}
