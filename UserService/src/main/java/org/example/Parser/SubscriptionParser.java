package org.example.Parser;

import org.example.DTO.SubscriptionDTO;

public class SubscriptionParser {

    public void parse(String destination) {

        String[] parts = destination.split("/");
        if (parts.length >= 4) {
            String owner = parts[2];
            String repo = parts[3];
            String branch = parts.length > 4 ? parts[4] : "default";


        }
    }
}
