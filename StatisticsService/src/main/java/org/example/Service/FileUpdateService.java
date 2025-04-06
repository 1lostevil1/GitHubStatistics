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
import org.example.Response.Github.Commit.FileStatus;
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

    private final ObjectMapper objectMapper;

    public FileUpdateService(FileRepo fileRepo, CommitRepo commitRepo, BranchRepo branchRepo) {
        this.fileRepo = fileRepo;
        this.commitRepo = commitRepo;
        this.branchRepo = branchRepo;
        this.objectMapper = new ObjectMapper();
    }

    void processFiles(String branchUrl, List<FileResponse> files) {

        if (!files.isEmpty()) {

            BranchEntity branchEntity = branchRepo.findByUrl(branchUrl).orElseThrow();

            for (FileResponse fileResponse : files) {

               if( (fileResponse.status().equals(FileStatus.RENAMED)&& commitRepo.findAllByCurrentNameAndBranch(fileResponse.filename(),branchEntity).isEmpty() ) || commitRepo.findByCurrentNameAndChangeSha(fileResponse.filename(),fileResponse.changeSha()).isEmpty()) {

                   switch (fileResponse.status()) {

                       case ADDED -> {

                           addedStateProcessing(branchEntity, fileResponse);
                       }


                       case REMOVED -> {

                           removedStateProcessing(branchEntity, fileResponse);
                       }

                       case RENAMED -> {
                           renamedStateProcessing(branchEntity, fileResponse);
                       }

                       case MODIFIED -> {

                           modifiedStateProcessing(branchEntity, fileResponse);
                       }

                       case CHANGED -> {
                           log.info("file {} changed", fileResponse.filename());
                       }

                       default -> {
                           log.info("file default status {} ", fileResponse.status());
                       }

                   }
               }

            }

        }
    }


    void addedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {


        Optional<FileEntity> fileEntityOptional = fileRepo.findByName(fileResponse.filename());

        Optional<CommitEntity> commitEntityOptional = commitRepo.findByCurrentNameAndBranch(fileResponse.filename(), branchEntity);

        if (fileEntityOptional.isEmpty()) {

            if (commitEntityOptional.isEmpty()) {
                FileEntity fileEntity = new FileEntity(fileResponse.filename());
                fileEntityOptional = Optional.of(fileEntity);
                fileRepo.saveAndFlush(fileEntity);
            }
        }

        CommitEntity commitEntity;

        if (commitEntityOptional.isEmpty()) {

            commitEntity = new CommitEntity(fileEntityOptional.get(),
                    branchEntity, fileResponse.additions(),
                    fileResponse.deletions(),
                    fileResponse.changes(),
                    fileResponse.filename(),
                    fileResponse.changeSha(),
                    fileResponse.previousFilename(),
                    fileResponse.status().name());

        }
        //комит с добавлением этого файла уже есть, возможный кейс - неадекватный мердж комит
        //или если файл был удалён
        else {

            commitEntity = commitEntityOptional.get();
            commitEntity.setCurrentName(fileResponse.filename());
            commitEntity.setState(fileResponse.status().name());
            commitEntity.setAdditions(fileResponse.additions());
            commitEntity.setDeletions(fileResponse.deletions());
            commitEntity.setChanges(fileResponse.changes());
            commitEntity.setChangeSha(fileResponse.changeSha());
            commitEntity.setPreviousNames(fileResponse.previousFilename());
        }

        commitRepo.saveAndFlush(commitEntity);
    }


    void removedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {


        CommitEntity commitEntity = commitRepo.findByCurrentNameAndBranch(fileResponse.filename(), branchEntity).orElseThrow();

        commitEntity.setState(fileResponse.status().name());
        commitEntity.setChangeSha(fileResponse.changeSha());
        commitRepo.saveAndFlush(commitEntity);
    }


    void renamedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {

        try {
            CommitEntity commitEntity = commitRepo.findByCurrentNameAndBranch(fileResponse.previousFilename(), branchEntity).orElseThrow();

            try {
                List<String> previousNames = new ArrayList<>();


                if (!(commitEntity.getPreviousNames() == null)) {

                    previousNames = objectMapper.readValue(commitEntity.getPreviousNames(), new TypeReference<List<String>>() {
                    });
                }

                previousNames.add(fileResponse.previousFilename());

                commitEntity.setCurrentName(fileResponse.filename());
                commitEntity.setChangeSha(fileResponse.changeSha());
                commitEntity.setPreviousNames(objectMapper.writeValueAsString(previousNames));

                commitRepo.saveAndFlush(commitEntity);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchElementException e) {
            log.error(fileResponse.toString());
        }

    }


    void modifiedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse) {

        try {
            CommitEntity commitEntity = commitRepo.findByCurrentNameAndBranch(fileResponse.filename(), branchEntity).orElseThrow();

            commitEntity.setState(fileResponse.status().name());
            commitEntity.setAdditions(commitEntity.getAdditions() + fileResponse.additions());
            commitEntity.setDeletions(commitEntity.getDeletions() + fileResponse.deletions());
            commitEntity.setChanges(commitEntity.getChanges() + fileResponse.changes());
            commitEntity.setChangeSha(fileResponse.changeSha());
            commitRepo.saveAndFlush(commitEntity);
        } catch (Exception e){
            log.error(fileResponse.toString());
        }

    }


}
