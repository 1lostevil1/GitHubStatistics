package org.example.Request.Github;

import java.util.Set;

public record UpdateRequest(

        Set<String> branches
) {
}
