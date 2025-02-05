package org.example.Request.Github;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Response.Github.Commit.FileResponse;

import java.util.List;

public record UpdateRequest(
        @JsonProperty("owner") String owner,
        @JsonProperty("repo") String repo,
        @JsonProperty("branchName") String branchName,
        @JsonProperty("files") List<FileResponse> files
        ) {
}
