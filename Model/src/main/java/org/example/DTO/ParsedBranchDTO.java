package org.example.DTO;

import java.time.OffsetDateTime;

public record ParsedBranchDTO(
        String owner,
        String repo,
        String branchName
) {
}
