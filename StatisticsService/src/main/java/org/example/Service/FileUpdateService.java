package org.example.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.Entities.BranchEntity;
import org.example.Entities.CommitEntity;
import org.example.Entities.FileEntity;
import org.example.Parser.PatchParser;
import org.example.Repository.BranchRepo;
import org.example.Repository.CommitRepo;
import org.example.Repository.FileRepo;
import org.example.Response.Github.Commit.Author;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.FileResponse;
import org.example.Response.Github.Commit.FileStatus;
import org.example.Response.Github.Patch.PatchChangesResponse;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class FileUpdateService {

    private final FileRepo fileRepo;

    private final CommitRepo commitRepo;

    private final BranchRepo branchRepo;

    private final ObjectMapper objectMapper;

    private final PatchParser patchParser;

    public FileUpdateService(FileRepo fileRepo, CommitRepo commitRepo, BranchRepo branchRepo) {
        this.fileRepo = fileRepo;
        this.commitRepo = commitRepo;
        this.branchRepo = branchRepo;
        this.objectMapper = new ObjectMapper();
        this.patchParser = new PatchParser();
    }

    void processFiles(String branchUrl, CommitResponse commitResponse) {

        List<FileResponse> files = commitResponse.files();

        Author author = commitResponse.commit().author();

        if (!files.isEmpty()) {

            BranchEntity branchEntity = branchRepo.findByUrl(branchUrl).orElseThrow();

            for (FileResponse fileResponse : files) {

                PatchChangesResponse patchChangesResponse = patchParser.parsePatch(fileResponse);

               if( (fileResponse.getStatus().equals(FileStatus.RENAMED)&& commitRepo.findAllByCurrentNameAndBranch(fileResponse.getFilename(),branchEntity).isEmpty() ) ||
                       commitRepo.findByCurrentNameAndBranchAndChangeSha(fileResponse.getFilename(),branchEntity, fileResponse.getChangeSha()).isEmpty()) {

                   switch (fileResponse.getStatus()) {

                       case ADDED -> {

                           addedStateProcessing(branchEntity, fileResponse,author,patchChangesResponse);
                       }


                       case REMOVED -> {

                           removedStateProcessing(branchEntity, fileResponse,author,patchChangesResponse);
                       }

                       case RENAMED -> {
                           renamedStateProcessing(branchEntity, fileResponse,author,patchChangesResponse);
                       }

                       case MODIFIED -> {

                           modifiedStateProcessing(branchEntity, fileResponse,author,patchChangesResponse);
                       }

                       case CHANGED -> {
                           log.info("file {} changed", fileResponse.getFilename());
                       }

                       default -> {
                           log.info("file default status {} ", fileResponse.getStatus());
                       }

                   }
               }

            }

        }
    }


    void addedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse, Author author,PatchChangesResponse patchChangesResponse) {

        try {
            int refactors = processRefactored(branchEntity, fileResponse, patchChangesResponse);

            Optional<FileEntity> fileEntityOptional = fileRepo.findByName(fileResponse.getFilename());

            Optional<CommitEntity> commitEntityOptional = commitRepo.findByCurrentNameAndBranch(fileResponse.getFilename(), branchEntity);

            if (fileEntityOptional.isEmpty()) {

                if (commitEntityOptional.isEmpty()) {
                    FileEntity fileEntity = new FileEntity(fileResponse.getFilename());
                    fileEntityOptional = Optional.of(fileEntity);
                    fileRepo.saveAndFlush(fileEntity);
                }
            }

            CommitEntity commitEntity;

            if (commitEntityOptional.isEmpty()) {

                commitEntity = new CommitEntity(fileEntityOptional.get(),
                        branchEntity,
                        author.name(),
                        author.date(),
                        fileResponse.getAdditions(),
                        fileResponse.getDeletions(),
                        refactors,
                        fileResponse.getChanges(),
                        fileResponse.getFilename(),
                        fileResponse.getChangeSha(),
                        fileResponse.getPreviousFilename(),
                        patchChangesResponse.added(),
                        patchChangesResponse.removed(),
                        fileResponse.getStatus().name());

            }
            //комит с добавлением этого файла уже есть, возможный кейс - мердж комит
            //или если файл был удалён
            else {

                commitEntity = commitEntityOptional.get();
                commitEntity.setCurrentName(fileResponse.getFilename());
                commitEntity.setAuthor(author.name());
                commitEntity.setDate(author.date());
                commitEntity.setState(fileResponse.getStatus().name());
                commitEntity.setAdditions(fileResponse.getAdditions());
                commitEntity.setDeletions(fileResponse.getDeletions());
                commitEntity.setChanges(fileResponse.getChanges());
                commitEntity.setChangeSha(fileResponse.getChangeSha());
                commitEntity.setPreviousNames(fileResponse.getPreviousFilename());
                commitEntity.setRefactors(commitEntity.getRefactors()+refactors);
                commitEntity.setAddedStrings(patchChangesResponse.added());
            }

            commitRepo.saveAndFlush(commitEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    void removedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse, Author author,PatchChangesResponse patchChangesResponse) {

            //кейс на случай   hw2 : fileA->renamed->fileB, hw1:fileA->removed-X, hw1->merge->hw2
            if (commitRepo.findByCurrentNameAndBranch(fileResponse.getFilename(), branchEntity).isEmpty()) {
                return;
            }
try {
    int refactors = processRefactored(branchEntity, fileResponse, patchChangesResponse);

    CommitEntity commitEntity = commitRepo.findByCurrentNameAndBranch(fileResponse.getFilename(), branchEntity).orElseThrow();

    commitEntity.setState(fileResponse.getStatus().name());
    commitEntity.setChangeSha(fileResponse.getChangeSha());
    commitEntity.setDeletions(fileResponse.getDeletions());
    commitEntity.setAuthor(author.name());
    commitEntity.setDate(author.date());
    commitEntity.setRefactors(commitEntity.getRefactors()+refactors);
    commitEntity.setDeletedStrings(patchChangesResponse.removed());
    commitRepo.saveAndFlush(commitEntity);
} catch (Exception e) {
    throw new RuntimeException(e);
}

    }


    void renamedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse, Author author,PatchChangesResponse patchChangesResponse) {

        //кейс на случай   hw2 : fileA->renamed->fileB, hw1:fileA->removed-X, hw1->merge->hw2
        if (commitRepo.findByCurrentNameAndBranch(fileResponse.getPreviousFilename(), branchEntity).isEmpty()) {
            return;
        }

        try {
            CommitEntity commitEntity = commitRepo.findByCurrentNameAndBranch(fileResponse.getPreviousFilename(), branchEntity).orElseThrow();

            try {
                List<String> previousNames = new ArrayList<>();


                if (!(commitEntity.getPreviousNames() == null)) {

                    previousNames = objectMapper.readValue(commitEntity.getPreviousNames(), new TypeReference<List<String>>() {
                    });
                }

                previousNames.add(fileResponse.getPreviousFilename());

                commitEntity.setCurrentName(fileResponse.getFilename());
                commitEntity.setChangeSha(fileResponse.getChangeSha());
                commitEntity.setPreviousNames(objectMapper.writeValueAsString(previousNames));
                commitEntity.setAuthor(author.name());
                commitEntity.setDate(author.date());

                commitRepo.saveAndFlush(commitEntity);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchElementException e) {
            log.error(fileResponse.toString());
        }

    }


    void modifiedStateProcessing(BranchEntity branchEntity, FileResponse fileResponse, Author author,PatchChangesResponse patchChangesResponse) {

        //кейс на случай   hw2 : fileA->renamed->fileB, hw1:fileA->removed-X, hw1->merge->hw2
        if (commitRepo.findByCurrentNameAndBranch(fileResponse.getFilename(), branchEntity).isEmpty()) {
            return;
        }


        try {
            CommitEntity commitEntity = commitRepo.findByCurrentNameAndBranch(fileResponse.getFilename(), branchEntity).orElseThrow();

            int refactors = processRefactored(branchEntity,fileResponse,patchChangesResponse);

            commitEntity.setState(fileResponse.getStatus().name());
            commitEntity.setAdditions(commitEntity.getAdditions() + fileResponse.getAdditions());
            commitEntity.setDeletions(commitEntity.getDeletions() + fileResponse.getDeletions());
            commitEntity.setChanges(commitEntity.getChanges() + fileResponse.getChanges());
            commitEntity.setChangeSha(fileResponse.getChangeSha());
            commitEntity.setAuthor(author.name());
            commitEntity.setDate(author.date());
            commitEntity.setRefactors(commitEntity.getRefactors()+refactors);
            commitEntity.setDeletedStrings(patchChangesResponse.removed());
            commitEntity.setAddedStrings(patchChangesResponse.added());
            commitRepo.saveAndFlush(commitEntity);
        } catch (Exception e){
            log.error(fileResponse.toString());
        }

    }




    private int processRefactored(BranchEntity branchEntity, FileResponse fileResponse,PatchChangesResponse patchChangesResponse) throws JsonProcessingException {

        // Разбиваем строки на списки
        List<String> addedLines = objectMapper.readValue(
                patchChangesResponse.added(),
                new TypeReference<List<String>>() {}
        );

        List<String> removedLines = objectMapper.readValue(
                patchChangesResponse.removed(),
                new TypeReference<List<String>>() {}
        );

        // Находим коммиты, где эти строки встречались ранее
        List<CommitEntity> matchingPreviouslyDeleted = commitRepo
                .findAllByCurrentNameAndBranchAndDeletedStringsContaining(
                        fileResponse.getFilename(), branchEntity, patchChangesResponse.added()
                );

        List<CommitEntity> matchingPreviouslyAdded = commitRepo
                .findAllByCurrentNameAndBranchAndAddedStringsContaining(
                        fileResponse.getFilename(), branchEntity, patchChangesResponse.removed()
                );

        List<String> previouslyDeletedStrings = new ArrayList<>();
        List<String> previouslyAddedStrings = new ArrayList<>();

        for (CommitEntity commitEntity : matchingPreviouslyDeleted) {
            List<String> deleted = objectMapper.readValue(
                    commitEntity.getDeletedStrings(), new TypeReference<>() {}
            );
            previouslyDeletedStrings.addAll(deleted);
        }

        for (CommitEntity commitEntity : matchingPreviouslyAdded) {
            List<String> added = objectMapper.readValue(
                    commitEntity.getAddedStrings(), new TypeReference<>() {}
            );
            previouslyAddedStrings.addAll(added);
        }

        // Считаем рефакторинг: удалено и где-то ранее добавлялось, или наоборот
        int refactoredCount = 0;

        for (String line : addedLines) {
            if (previouslyDeletedStrings.contains(line)) {
                refactoredCount++;
            }
        }

        for (String line : removedLines) {
            if (previouslyAddedStrings.contains(line)) {
                refactoredCount++;
            }
        }

        // Рефакторинг в рамках текущего коммита (вставили и удалили одну и ту же строку)
        Set<String> internalRefactored = new HashSet<>(addedLines);
        internalRefactored.retainAll(removedLines);

        refactoredCount += internalRefactored.size();


        for (CommitEntity commitEntity : matchingPreviouslyDeleted) {
            commitEntity.setRefactors(refactoredCount); // <-- твоё поле
        }

        for (CommitEntity commitEntity : matchingPreviouslyAdded) {
            commitEntity.setRefactors(refactoredCount); // <-- твоя логика может отличаться
        }

        // Сохраняем в репозиторий (если это JPA)
        commitRepo.saveAll(matchingPreviouslyDeleted);
        commitRepo.saveAll(matchingPreviouslyAdded);

        return refactoredCount;
    }
}
