package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stats(
        @JsonProperty("total")  int total,
        @JsonProperty("additions") int additions,
        @JsonProperty("deletions") int deletions)
{
}
