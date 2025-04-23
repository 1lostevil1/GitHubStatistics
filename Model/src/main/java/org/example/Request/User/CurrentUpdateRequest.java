package org.example.Request.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Response.Update.FileUpdateResponse;

import java.util.List;

public record CurrentUpdateRequest(@JsonProperty("username") String username,
                                   @JsonProperty("updateRequest") UpdateRequest updateRequest) {
}
