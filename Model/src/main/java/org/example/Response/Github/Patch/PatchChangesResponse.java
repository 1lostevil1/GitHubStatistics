package org.example.Response.Github.Patch;

import java.util.List;

public record PatchChangesResponse(String fileName,
                                   String added,
                                   String removed) {
}
