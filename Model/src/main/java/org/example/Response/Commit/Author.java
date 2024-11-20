package org.example.Response.Commit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Author(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("login") String login,
        @JsonProperty("id") int id) {
}
