package org.example.Request.Github;

import java.time.OffsetDateTime;

public record DatedListCommitRequest(
        String owner,
        String repo,
        String branch,
        OffsetDateTime since,
        OffsetDateTime until
) {

}
