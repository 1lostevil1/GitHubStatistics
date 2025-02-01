package org.example.Response.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegistrationResponse(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email
) {
}
