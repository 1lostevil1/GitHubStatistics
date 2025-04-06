package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Author(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("login") String login,
        @JsonProperty("date") OffsetDateTime date,
        @JsonProperty("id") int id) {
}
