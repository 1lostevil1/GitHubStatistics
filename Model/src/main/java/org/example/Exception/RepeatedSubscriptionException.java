package org.example.Exception;

public record RepeatedSubscriptionException(
        int status,
        String message
) {
}
