package org.example.Exception;

import java.util.Date;

public record RepeatedRegistrationException(
        int status,
        String message
) {
}
