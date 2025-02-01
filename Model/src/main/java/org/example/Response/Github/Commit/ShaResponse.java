package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShaResponse(
        @JsonProperty("sha") String sha
) {
}
