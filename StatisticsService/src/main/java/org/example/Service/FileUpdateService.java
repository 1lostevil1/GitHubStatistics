package org.example.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.Entities.BranchEntity;
import org.example.Entities.CommitEntity;
import org.example.Entities.FileEntity;
import org.example.Repository.BranchRepo;
import org.example.Repository.CommitRepo;
import org.example.Repository.FileRepo;
import org.example.Response.Github.Commit.FileResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Slf4j
public class FileUpdateService {

    private final FileRepo fileRepo;

    private final CommitRepo commitRepo;

    private final BranchRepo branchRepo;

    public FileUpdateService(FileRepo fileRepo, CommitRepo commitRepo, BranchRepo branchRepo) {
        this.fileRepo = fileRepo;
        this.commitRepo = commitRepo;
        this.branchRepo = branchRepo;
    }

    void processFiles(String branchUrl, List<FileResponse> files) {

        if (!files.isEmpty()) {

            BranchEntity branchEntity = branchRepo.findByUrl(branchUrl).orElseThrow();

            for (FileResponse fileResponse : files) {

                log.info("--------file/status:::   {}   {}", fileResponse.filename(), fileResponse.status());

                switch (fileResponse.status()) {

                    case ADDED -> {

                        addedStateProcessing(branchEntity, fileResponse);
                    }


                    case REMOVED -> {

                        removedStateProcessing(branchEntity, fileResponse);
                    }

                    case RENAMED -> {
                        renamedStateProcessing(branchEntity,fileResponse);
                    }

                    case MODIFIED -> {

                        modifiedStateProcessing(branchEntity, fileResponse);
                    }

                    case CHANGED -> {
                        log.info("file {} changed", fileResponse.filename());
                    }

                    default -> {
                        log.info("file status {} ", fileResponse.status());
                    }

                }

            }

        }
    }





    void addedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {

        Optional<FileEntity> fileEntityOptional = fileRepo.findByName(fileResponse.filename());


        if (fileEntityOptional.isEmpty()) {
            FileEntity fileEntity = new FileEntity(fileResponse.filename());
            fileRepo.saveAndFlush(fileEntity);

            CommitEntity commitEntity = new CommitEntity(fileEntity,
                    branchEntity, fileResponse.additions(),
                    fileResponse.deletions(),
                    fileResponse.changes(),
                    fileResponse.previousFilename(),
                    fileResponse.status().name());
            commitRepo.saveAndFlush(commitEntity);
        } else
        {

            Optional<CommitEntity> commitEntityOptional = commitRepo.findByBranchAndFile(branchEntity, fileEntityOptional.get());
            CommitEntity commitEntity;
            if (commitEntityOptional.isPresent()) {
                commitEntity = commitEntityOptional.get();
                commitEntity.setState(fileResponse.status().name());
                commitEntity.setAdditions(fileResponse.additions());
                commitEntity.setDeletions(fileResponse.deletions());
                commitEntity.setChanges(fileResponse.changes());
            }

            else
            {

                commitEntity = new CommitEntity(fileEntityOptional.get(),
                        branchEntity, fileResponse.additions(),
                        fileResponse.deletions(),
                        fileResponse.changes(),
                        fileResponse.previousFilename(),
                        fileResponse.status().name());
            }
            commitRepo.saveAndFlush(commitEntity);

        }



    }


    void removedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {

        Optional<FileEntity> fileEntityOptional = fileRepo.findByName(fileResponse.filename());
        if(fileEntityOptional.isPresent()) {
            CommitEntity commitEntity = commitRepo.findByBranchAndFile(branchEntity, fileEntityOptional.get()).orElseThrow();
            commitEntity.setState(fileResponse.status().name());
            commitRepo.saveAndFlush(commitEntity);
        }


    }




    void renamedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {

        ObjectMapper objectMapper = new ObjectMapper();


        Optional<FileEntity> fileEntityOptional = fileRepo.findByName(fileResponse.previousFilename());

try {

    CommitEntity commitEntity = commitRepo.findByBranchAndFile(branchEntity, fileEntityOptional.get()).orElseThrow();

    try {
        List<String> previousNames = new ArrayList<>();

        if (!(commitEntity.getPreviousNames() == null)) {
            previousNames = objectMapper.readValue(commitEntity.getPreviousNames(), new TypeReference<List<String>>() {
            });
        }


        commitEntity.setState(fileResponse.status().name());
        List<String> newPreviousNames = new ArrayList<>();

        if (previousNames.isEmpty()) {

            newPreviousNames.add(fileResponse.previousFilename());

        } else {

            newPreviousNames.addAll(previousNames);
            newPreviousNames.add(fileResponse.previousFilename());

        }
        commitEntity.setPreviousNames(objectMapper.writeValueAsString(newPreviousNames));
        //TODO А ГДЕ ХРАНИТЬ НОВОЕ ИМЯ? (поле в комите для нового имени?)
        removedStateProcessing(branchEntity, new FileResponse(fileResponse.previousFilename(),fileResponse.status(),fileResponse.additions(),fileResponse.deletions(),fileResponse.changes(),fileResponse.previousFilename()));
        addedStateProcessing(branchEntity, fileResponse);

    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }
} catch (Exception e) {
    log.info("no such file in DB when RENAMED, pr?:  {}", fileResponse.filename());
    addedStateProcessing(branchEntity, fileResponse);
}

    }

    void modifiedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {

        Optional<FileEntity> fileEntityOptional  = fileRepo.findByName(fileResponse.filename());

        if(fileEntityOptional.isPresent()) {
            CommitEntity commitEntity = commitRepo.findByBranchAndFile(branchEntity, fileEntityOptional.get()).orElseThrow();

            commitEntity.setState(fileResponse.status().name());
            commitEntity.setAdditions(commitEntity.getAdditions() + fileResponse.additions());
            commitEntity.setDeletions(commitEntity.getDeletions() + fileResponse.deletions());
            commitEntity.setChanges(commitEntity.getChanges() + fileResponse.changes());
            commitRepo.saveAndFlush(commitEntity);
        }
        else{

            log.info("no such file in DB when MODIFIED, pr?:  {}", fileResponse.filename());
            addedStateProcessing(branchEntity, fileResponse);
        }


    }


}
