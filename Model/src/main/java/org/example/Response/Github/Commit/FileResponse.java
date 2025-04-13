package org.example.Response.Github.Commit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FileResponse {

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("sha")
    private String changeSha;

    @JsonProperty("status")
    private FileStatus status;

    @JsonProperty("additions")
    private int additions;

    @JsonProperty("deletions")
    private int deletions;

    @JsonProperty("changes")
    private int changes;

    @JsonProperty("previous_filename")
    private String previousFilename;

    @JsonProperty("patch")
    private String patch = "";
}
