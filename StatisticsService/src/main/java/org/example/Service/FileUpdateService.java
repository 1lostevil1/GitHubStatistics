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

                Optional<FileEntity> fileEntityOptional = fileRepo.findByName(fileResponse.filename());

                switch (fileResponse.status()) {

                    case ADDED -> {

                        addedStateProcessing(branchEntity, fileEntityOptional, fileResponse);
                    }


                    case REMOVED -> {

                        removedStateProcessing(branchEntity, fileEntityOptional, fileResponse);
                    }

                    case RENAMED -> {
                        renamedStateProcessing(branchEntity, fileEntityOptional, fileResponse);
                    }

                    case MODIFIED -> {

                        modifiedStateProcessing(branchEntity, fileEntityOptional, fileResponse);
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


    void addedStateProcessing(BranchEntity branchEntity, Optional<FileEntity> fileEntityOptional, FileResponse fileResponse) {

        CommitEntity commitEntity;

        if (fileEntityOptional.isEmpty()) {
            fileRepo.saveAndFlush(new FileEntity(fileResponse.filename()));

            commitEntity = new CommitEntity(fileEntityOptional.get(),
                    branchEntity, fileResponse.additions(),
                    fileResponse.deletions(),
                    fileResponse.changes(),
                    fileResponse.previousFilename(),
                    fileResponse.status().name());
        } else {

            commitEntity = commitRepo.findByBranchAndFile(branchEntity, fileEntityOptional.get()).orElseThrow();

            commitEntity.setState(fileResponse.status().name());
            commitEntity.setAdditions(fileResponse.additions());
            commitEntity.setDeletions(fileResponse.deletions());
            commitEntity.setChanges(fileResponse.changes());

        }

        commitRepo.saveAndFlush(commitEntity);

    }


    void removedStateProcessing(BranchEntity branchEntity, Optional<FileEntity> fileEntityOptional, FileResponse fileResponse) {

        CommitEntity commitEntity = commitRepo.findByBranchAndFile(branchEntity, fileEntityOptional.get()).orElseThrow();
        commitEntity.setState(fileResponse.status().name());
        commitRepo.saveAndFlush(commitEntity);

    }

    void renamedStateProcessing(BranchEntity branchEntity, Optional<FileEntity> fileEntityOptional, FileResponse fileResponse) {

        FileEntity fileEntity = fileEntityOptional.orElseThrow();

        ObjectMapper objectMapper = new ObjectMapper();

        CommitEntity commitEntity = commitRepo.findByBranchAndFile(branchEntity, fileEntity).orElseThrow();

        try {
            List<String> previousNames = objectMapper.readValue(commitEntity.getPreviousNames(), new TypeReference<List<String>>() {
            });

            commitEntity.setState(fileResponse.status().name());
            List<String> newPreviousNames = new ArrayList<>();

            if (previousNames.isEmpty()) {

                newPreviousNames.add(fileResponse.previousFilename());

            } else {

                newPreviousNames.addAll(previousNames);
                newPreviousNames.add(fileResponse.previousFilename());

            }
            commitEntity.setPreviousNames(objectMapper.writeValueAsString(newPreviousNames));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    void modifiedStateProcessing(BranchEntity branchEntity, Optional<FileEntity> fileEntityOptional, FileResponse fileResponse) {

        FileEntity fileEntity = fileEntityOptional.orElseThrow();

        CommitEntity commitEntity = commitRepo.findByBranchAndFile(branchEntity, fileEntity).orElseThrow();

        commitEntity.setState(fileResponse.status().name());
        commitEntity.setAdditions(commitEntity.getAdditions() + fileResponse.additions());
        commitEntity.setDeletions(commitEntity.getDeletions() + fileResponse.deletions());
        commitEntity.setChanges(commitEntity.getChanges() + fileResponse.changes());
        commitRepo.saveAndFlush(commitEntity);


    }


}
