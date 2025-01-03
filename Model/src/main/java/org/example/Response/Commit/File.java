package org.example.Response.Commit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record File(
        @JsonProperty("filename") String filename,
        @JsonProperty("status") String status,
        @JsonProperty("additions") int additions,
        @JsonProperty("deletions") int deletions,
        @JsonProperty("changes") int changes,
        @JsonProperty("previous_filename") String previousFilename
) {
}
