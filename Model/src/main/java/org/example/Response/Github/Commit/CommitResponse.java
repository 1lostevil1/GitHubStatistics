package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitResponse(
        @JsonProperty("url") String url,
        @JsonProperty("sha") String sha,
        @JsonProperty("files") List<FileResponse> files,
        @JsonProperty("stats") Stats stats,
        @JsonProperty("commit") Commit commit
) {
}
