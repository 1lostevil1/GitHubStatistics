package org.example.Response.Commit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitResponse(
        @JsonProperty("url") String url,
        @JsonProperty("sha") String sha,
        @JsonProperty("files") List<File> files,
        @JsonProperty("stats") Stats stats,
        @JsonProperty("author") Author author
) {
}
