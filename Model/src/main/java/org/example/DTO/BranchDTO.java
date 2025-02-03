package org.example.DTO;

import java.time.OffsetDateTime;

public record BranchDTO(
      String owner,
      String repo,
      String branchName,
      OffsetDateTime timestamp
)
{}
