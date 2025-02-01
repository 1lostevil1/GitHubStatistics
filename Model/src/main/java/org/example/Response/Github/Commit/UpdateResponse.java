package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UpdateResponse(
        @JsonProperty("owner") String owner,
        @JsonProperty("owner") String repo,
        @JsonProperty("owner") String branchName,
        @JsonProperty("files") List<File> files
        ) {
}
