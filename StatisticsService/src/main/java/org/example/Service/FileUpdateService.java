package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.FileDTO;
import org.example.Entities.Github.FileEntity;
import org.example.Repository.FileRepo;
import org.example.Utils.FileMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
                        log.info("{} ADDED", fileDTO);
                    }
                    case REMOVED -> {

                        if(fileRepo.findByName(fileDTO.filename()).isPresent()){
                            fileRepo.updateName(fileDTO.filename()+" $REMOVED$", fileDTO.filename());
                        }
                        else {
                            fileRepo.saveAndFlush(fileMapper.DTOToFileEntity(fileDTO));
                        }
                        log.info("{} REMOVED", fileDTO);
                    }

                    case RENAMED -> {

                        if(fileRepo.findByName(fileDTO.filename()).isPresent()){
                            fileRepo.updateName(fileDTO.filename()+" $RENAMED$", fileDTO.previousFilename());
                        }
                        else {
                            fileRepo.saveAndFlush(fileMapper.DTOToFileEntity(fileDTO));
                        }
                        log.info("{} RENAMED ", fileDTO);
                    }

                    case MODIFIED -> {
                        log.info("{} MODIFIED ", fileDTO);
                        try {
                            Optional<FileEntity> fileEntityOptional = fileRepo.findByName(fileDTO.filename());

                            if (fileEntityOptional.isPresent()) {
                                FileEntity fileEntity = fileEntityOptional.get();
                                fileEntity.setAdditions(fileDTO.additions());
                                fileEntity.setDeletions(fileDTO.deletions());
                                fileEntity.setChanges(fileDTO.changes());
                                fileRepo.saveAndFlush(fileEntity);
                            } else {
                                fileRepo.saveAndFlush(fileMapper.DTOToFileEntity(fileDTO));
                            }
                        } catch (Exception e) {
                            log.info(e.getMessage());
                        }
                    }

                    case CHANGED -> {
                        log.info("file {} changed", fileDTO.filename());
                    }

                    default -> {
                        log.info("file status {} ", fileDTO.status());
                    }

                }

            }

        }
    }
}
