package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FileStatus {
    ADDED,
    REMOVED,
    MODIFIED,
    RENAMED,
    COPIED,
    CHANGED,
    UNCHANGED;

    @JsonCreator
    public static FileStatus fromString(String value) {
        return FileStatus.valueOf(value.toUpperCase());
    }
}
