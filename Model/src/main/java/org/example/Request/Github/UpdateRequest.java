package org.example.Request.Github;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Response.Github.Commit.FileResponse;

import java.util.List;

public record UpdateRequest(
        @JsonProperty("owner") String owner,
        @JsonProperty("owner") String repo,
        @JsonProperty("owner") String branchName,
        @JsonProperty("files") List<FileResponse> files
        ) {
}
