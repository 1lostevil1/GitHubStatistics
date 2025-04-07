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
import org.example.Request.User.UpdateRequest;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.FileStatus;
import org.example.Response.Update.FileUpdateResponse;
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

    private final CommitRepo commitRepo;

    private final GitHubClient gitHubClient;

    private final UserClient userClient;

    private final UrlParser urlParser;

    private final FileUpdateService fileUpdateService;

    private final ExecutorService executorService ;


    public void initialSub(BranchDTO link) {



        log.info("----initial sub");
        BranchEntity branchEntity= branchRepo.findByUrl(link.url()).orElseThrow();

        UpdateRequest updateRequest;

        if (branchEntity.getCheckAt().isAfter(OffsetDateTime.now())) {

            List<CommitResponse> commitResponses = getCommits(link);
            processCommits(commitResponses,link);
            updateRequest = collectUpdateRequestFromDB(link.url());
        }
        else {
            updateRequest = collectUpdateRequestFromDB(link.url());
        }

        log.info("--------- sending update   ---------");
        log.info(updateRequest.toString());
        userClient.sendUpdate(updateRequest);

        branchRepo.updateCheckAtByUrl(OffsetDateTime.now(),link.url());
    }





    public BranchUpdateService(BranchRepo branchRepo, GitHubClient gitHubClient, UserClient userClient, UrlParser urlParser, FileUpdateService fileUpdateService, CommitRepo commitRepo) {
        this.branchRepo = branchRepo;
        this.gitHubClient = gitHubClient;
        this.userClient = userClient;
        this.urlParser = urlParser;
        this.executorService = Executors.newFixedThreadPool(30);
        this.fileUpdateService = fileUpdateService;
        this.commitRepo = commitRepo;
    }


    public void checkUpdates(List<BranchDTO> links) {

        if (!links.isEmpty()) {

            for (BranchDTO link : links) {

                List<CommitResponse> commitResponses = getCommits(link);

                if (!commitResponses.isEmpty()) {

                    processCommits(commitResponses,link);
                    UpdateRequest updateRequest = collectUpdateRequestFromDB(link.url());

                    log.info(updateRequest.toString());
                    log.info("----sending update   ---------");
                    userClient.sendUpdate(updateRequest);
                }

                branchRepo.updateCheckAtByUrl(OffsetDateTime.now(),link.url());

            }

        }
    }







    private UpdateRequest collectUpdateRequestFromDB(String url) {

        BranchEntity branch = branchRepo.findByUrl(url).orElseThrow();

        List<FileUpdateResponse> fileResponses = new ArrayList<>();

        commitRepo.findByBranch(branch).forEach(commitEntity -> {

            FileUpdateResponse fileResponse = new FileUpdateResponse( commitEntity.getAuthor(),
                    commitEntity.getDate(),
                    commitEntity.getCurrentName(),
                    FileStatus.valueOf(commitEntity.getState()),
                    commitEntity.getAdditions(),
                    commitEntity.getDeletions(),
                    commitEntity.getChanges(),
                    commitEntity.getPreviousNames());
            fileResponses.add(fileResponse);
        });


        return new UpdateRequest(url,fileResponses);

    }

    private List<CommitResponse> getCommits(BranchDTO link) {

        ParsedBranchDTO parsedBranchDTO = urlParser.parse(link.url());

        DatedListCommitRequest datedListCommitRequest = new DatedListCommitRequest(parsedBranchDTO.owner(),
                parsedBranchDTO.repo(),
                parsedBranchDTO.branchName(),
                link.checkAt(), OffsetDateTime.now().plusYears(2)
        );

        log.info(datedListCommitRequest.toString());

        return gitHubClient.getInto(datedListCommitRequest);
    }

    private void processCommits(List<CommitResponse> commitResponses, BranchDTO link) {

            for (int i = commitResponses.size() - 1; i >= 0; i--) {

                fileUpdateService.processFiles(link.url(), commitResponses.get(i));
            }
    }

}


