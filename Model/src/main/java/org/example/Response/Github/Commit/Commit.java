package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Commit(@JsonProperty("author") Author author) {
}
