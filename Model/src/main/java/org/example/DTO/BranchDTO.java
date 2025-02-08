package org.example.DTO;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public record BranchDTO(
        String url,
        OffsetDateTime checkAt
)
{}