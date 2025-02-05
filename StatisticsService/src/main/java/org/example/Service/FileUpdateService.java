package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.FileDTO;
import org.example.Entities.Github.FileEntity;
import org.example.Repository.FileRepo;
import org.example.Utils.FileMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class FileUpdateService {

    private final FileMapper fileMapper;

    private final FileRepo fileRepo;

    public FileUpdateService(FileMapper fileMapper, FileRepo fileRepo) {
        this.fileMapper = fileMapper;

        this.fileRepo = fileRepo;
    }

    public void processFiles(List<FileDTO> files){

        if(!files.isEmpty()){

            for(FileDTO fileDTO : files){

                switch (fileDTO.status()){
                    case ADDED -> {
                      fileRepo.saveAndFlush(fileMapper.DTOToFileEntity(fileDTO));
                    }
                    case REMOVED -> {

                        if(fileRepo.findByName(fileDTO.filename()).isPresent()){
                            fileRepo.updateName(fileDTO.filename()+" $REMOVED$", fileDTO.filename());
                        }
                        else {
                            fileRepo.saveAndFlush(fileMapper.DTOToFileEntity(fileDTO));
                        }

                    }

                    case RENAMED -> {

                        if(fileRepo.findByName(fileDTO.filename()).isPresent()){
                            fileRepo.updateName(fileDTO.filename()+" $RENAMED$", fileDTO.previousFilename());
                        }
                        else {
                            fileRepo.saveAndFlush(fileMapper.DTOToFileEntity(fileDTO));
                        }

                    }

                    case MODIFIED -> {
                        FileEntity fileEntity = fileRepo.findByName(fileDTO.filename()).orElseThrow();
                        if(fileRepo.findByName(fileDTO.filename()).isPresent()) {
                            fileEntity.setAdditions(fileDTO.additions());
                            fileEntity.setDeletions(fileDTO.deletions());
                            fileEntity.setChanges(fileDTO.changes());
                            fileRepo.saveAndFlush(fileEntity);
                        }
                        else {
                            fileRepo.saveAndFlush(fileMapper.DTOToFileEntity(fileDTO));
                        }
                    }

                    case CHANGED -> {
                        log.info("file {} changed", fileDTO.filename());
                    }

                }

            }

        }
    }
}
