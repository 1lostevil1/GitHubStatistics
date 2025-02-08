package org.example.Service;

import lombok.extern.slf4j.Slf4j;
import org.example.Client.GitHubClient;
import org.example.Client.UserClient;
import org.example.DTO.BranchDTO;
import org.example.DTO.ParsedBranchDTO;
import org.example.Entities.BranchEntity;
import org.example.Parser.UrlParser;
import org.example.Repository.BranchRepo;
import org.example.Repository.CommitRepo;
import org.example.Request.Github.DatedListCommitRequest;
import org.example.Request.Github.UpdateRequest;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.FileResponse;
import org.example.Response.Github.Commit.FileStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class BranchUpdateService {

    private final BranchRepo branchRepo;

    private final GitHubClient gitHubClient;

    private final UserClient userClient;

    private final UrlParser urlParser;

    private final ExecutorService executorService;

    private final FileUpdateService fileUpdateService;
    private final CommitRepo commitRepo;


    public BranchUpdateService(BranchRepo branchRepo, GitHubClient gitHubClient, UserClient userClient, UrlParser urlParser, FileUpdateService fileUpdateService, CommitRepo commitRepo) {
        this.branchRepo = branchRepo;
        this.gitHubClient = gitHubClient;
        this.userClient = userClient;
        this.urlParser = urlParser;
        this.executorService = Executors.newFixedThreadPool(10);
        this.fileUpdateService = fileUpdateService;
        this.commitRepo = commitRepo;
    }


    public void checkUpdates(List<BranchDTO> links) {

        if (!links.isEmpty()) {

            for (BranchDTO link : links) {

//                executorService.submit(() -> {

                ParsedBranchDTO parsedBranchDTO = urlParser.parse(link.url());

                DatedListCommitRequest datedListCommitRequest = new DatedListCommitRequest(parsedBranchDTO.owner(),
                        parsedBranchDTO.repo(),
                        parsedBranchDTO.branchName(),
                        link.checkAt(), OffsetDateTime.now()
                );

                List<CommitResponse> commitResponses = gitHubClient.getInto(datedListCommitRequest);


                if (!commitResponses.isEmpty()) {

                    for (int i = commitResponses.size() - 1; i >= 0; i--) {

                        fileUpdateService.processFiles(link.url(), commitResponses.get(i).files());
                    }

                    BranchEntity branch = branchRepo.findByUrl(link.url()).orElseThrow();

                    List<FileResponse> fileResponses = new ArrayList<>();

                    commitRepo.findByBranch(branch).forEach(commitEntity -> {

                        FileResponse fileResponse = new FileResponse(commitEntity.getFile().getName(),
                                                                     FileStatus.valueOf(commitEntity.getState()),
                                                                     commitEntity.getAdditions(),
                                                                     commitEntity.getDeletions(),
                                                                     commitEntity.getChanges(),
                                                                     commitEntity.getPreviousNames());
                        fileResponses.add(fileResponse);
                    });


                    UpdateRequest updateRequest = new UpdateRequest(link.url(), fileResponses);
                    userClient.sendUpdate(updateRequest);
                }


                branchRepo.updateTimestampByOwnerRepoAndBranchName(
                        OffsetDateTime.now(),
                        link.url()
                );

//                });
            }

        }
    }
}


