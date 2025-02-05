package org.example.DTO;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public record BranchDTO(
      String owner,
      String repo,
      String branchName,
      OffsetDateTime timestamp
)
{}
