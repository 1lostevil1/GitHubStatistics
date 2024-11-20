package org.example.Response.Commit;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stats(
        @JsonProperty("total")  int total,
        @JsonProperty("additions") int additions,
        @JsonProperty("deletions") int deletions)
{
}
