package org.example.Request.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriptionRequest(
        @JsonProperty("username") String username,
        @JsonProperty("owner") String owner,
        @JsonProperty("repo") String repo,
        @JsonProperty("branch_name") String branchName

) {
}
