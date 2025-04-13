package org.example.Parser;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Response.Github.Commit.FileResponse;
import org.example.Response.Github.Patch.PatchChangesResponse;

import java.util.*;

public class PatchParser {

    public PatchChangesResponse parsePatch(FileResponse fileResponse) {

        ObjectMapper objectMapper = new ObjectMapper();

        List<String> added = new ArrayList<>();
        List<String> removed = new ArrayList<>();

        String[] lines = fileResponse.getPatch().split("\n");

        for (String line : lines) {
            if (line.startsWith("+") && !line.startsWith("+++")) {
                added.add(line.substring(1));
            } else if (line.startsWith("-") && !line.startsWith("---")) {
                removed.add(line.substring(1));
            }
        }
        try {

            String addedJson = objectMapper.writeValueAsString(added);
            String removedJson = objectMapper.writeValueAsString(removed);

            return new PatchChangesResponse(fileResponse.getFilename(), addedJson, removedJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
