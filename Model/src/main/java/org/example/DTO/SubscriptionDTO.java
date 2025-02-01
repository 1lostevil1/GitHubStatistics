package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionDTO(
        @JsonProperty("username") String username,
        @JsonProperty("owner") String owner,
        @JsonProperty("repo") String repo,
        @JsonProperty("branch_name") String branchName
) {
}
