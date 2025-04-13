package org.example.Response.Update;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Response.Github.Commit.FileStatus;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FileUpdateResponse(  @JsonProperty("author") String author,
                                   @JsonProperty("date") OffsetDateTime date,
                                   @JsonProperty("filename") String filename,
                                   @JsonProperty("status") FileStatus status,
                                   @JsonProperty("additions") int additions,
                                   @JsonProperty("deletions") int deletions,
                                   @JsonProperty("refactors") int refactors,
                                   @JsonProperty("changes") int changes,
                                   @JsonProperty("previous_filename") String previousFilename) {
}
