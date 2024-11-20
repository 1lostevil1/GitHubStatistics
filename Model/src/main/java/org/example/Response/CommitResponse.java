package org.example.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitResponse(
        @JsonProperty("url") String url,
        @JsonProperty("sha") String sha,
        @JsonProperty("node_id") String nodeId,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("comments_url") String commentsUrl,
        @JsonProperty("commit") DifferencesPerCommit commit,
        @JsonProperty("author") Author author
) {
}
