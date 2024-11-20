package org.example.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DifferencesPerCommit(
        @JsonProperty("filename") String filename,
        @JsonProperty("status") String status,
        @JsonProperty("additions") int additions,
        @JsonProperty("deletions") int deletions,
        @JsonProperty("changes") int changes,
        @JsonProperty("previous_filename") String previousFilename
) {
}
