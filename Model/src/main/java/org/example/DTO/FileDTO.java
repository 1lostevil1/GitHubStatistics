package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Response.Github.Commit.FileStatus;

public record FileDTO(
        String filename,
        FileStatus status,
        int additions,
        int deletions,
        int changes,
        String previousFilename
) {
}
