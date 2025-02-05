package org.example.Service;

import lombok.extern.slf4j.Slf4j;
import org.example.Client.GitHubClient;
import org.example.Client.UserClient;
import org.example.DTO.BranchDTO;
import org.example.DTO.DatedListCommitRequest;
import org.example.DTO.FileDTO;
import org.example.Repository.BranchRepo;
import org.example.Repository.FileRepo;
import org.example.Request.Github.UpdateRequest;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.FileResponse;
import org.example.Utils.BranchMapper;
import org.example.Utils.FileMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class BranchUpdateService {

    private final GitHubClient gitHubClient;

    private final UserClient userClient;

    private final FileMapper fileMapper;

    private final BranchRepo branchRepo;

    private final BranchMapper branchMapper;

    private final ExecutorService executorService;

    private final FileUpdateService fileUpdateService;



    public BranchUpdateService (GitHubClient gitHubClient, BranchRepo branchRepo, UserClient userClient, FileRepo fileRepo, FileMapper fileMapper, BranchMapper branchMapper, FileUpdateService fileUpdateService) {

        this.gitHubClient = gitHubClient;
        this.branchRepo = branchRepo;
        this.userClient = userClient;
        this.fileMapper = fileMapper;
        this.branchMapper = branchMapper;
        this.fileUpdateService = fileUpdateService;
        this.executorService = Executors.newFixedThreadPool(10);

    }


    public void checkUpdates(List<BranchDTO> links) {

        if (!links.isEmpty()) {

            for (BranchDTO link : links) {

//                executorService.submit(() -> {


                    DatedListCommitRequest datedListCommitRequest = branchMapper.branchDTOtoDatedListCommitRequest(link);
                    List<CommitResponse> commitResponses = gitHubClient.getInto(datedListCommitRequest);

                    if (!commitResponses.isEmpty()) {
                        UpdateRequest updateRequest = branchMapper.commitListToUpdateRequest(link, commitResponses);

                        List<FileDTO> files = updateRequest.files().stream().map(fileMapper::fileResponseToDTO).toList();

                        fileUpdateService.processFiles(files);

                        log.info("sending updates...");

                        userClient.sendUpdate(updateRequest);
                    }


                    branchRepo.updateTimestampByOwnerRepoAndBranchName(
                           OffsetDateTime.now(),
                            link.owner(),
                            link.repo(),
                            link.branchName()
                    );

//                });
            }

        }
    }
}


