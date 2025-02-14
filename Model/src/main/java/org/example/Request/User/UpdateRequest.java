package org.example.Request.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Response.Github.Commit.FileResponse;

import java.util.List;

public record UpdateRequest(
        @JsonProperty("branch") String branch,
        @JsonProperty("files") List<FileResponse> files
        ) { }
