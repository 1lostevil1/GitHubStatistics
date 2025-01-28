package org.example.Exception;

import java.util.Date;

public record WrongDataException(
        int status,
        String message
) {
}
