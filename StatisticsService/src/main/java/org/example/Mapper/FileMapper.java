package org.example.Mapper;

import org.example.Entities.FileEntity;
import org.example.Response.Github.Commit.FileResponse;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    public  FileEntity ResponseToFileEntity(FileResponse fileResponse) {
        return new FileEntity(fileResponse.filename());
    }

}
