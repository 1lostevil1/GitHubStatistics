package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDTO(
     String username,
     String email,
     int passwordHash
)  {
}
