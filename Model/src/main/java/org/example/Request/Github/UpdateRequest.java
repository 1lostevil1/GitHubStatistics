package org.example.Request.Github;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Response.Github.Commit.FileResponse;

import java.util.List;

public record UpdateRequest(
        @JsonProperty("topic") String topic,
        @JsonProperty("files") List<FileResponse> files
        ) { }
