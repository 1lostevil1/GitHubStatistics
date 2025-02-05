package org.example.Utils;

import org.example.DTO.FileDTO;
import org.example.Entities.Github.FileEntity;
import org.example.Response.Github.Commit.FileResponse;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    public FileDTO fileResponseToDTO(FileResponse fileResponse) {
        return  new FileDTO(fileResponse.filename(),fileResponse.status(),fileResponse.additions(),fileResponse.deletions(),fileResponse.changes(),fileResponse.previousFilename());
    }


    public  FileEntity DTOToFileEntity(FileDTO fileDTO) {
        return new FileEntity(fileDTO.filename(),fileDTO.additions(),fileDTO.deletions(),fileDTO.changes());
    }

}
