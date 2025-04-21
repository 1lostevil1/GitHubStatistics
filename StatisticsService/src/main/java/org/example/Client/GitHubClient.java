package org.example.Client;

import lombok.extern.slf4j.Slf4j;
import org.example.Request.Github.DatedListCommitRequest;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.FileResponse;
import org.example.Response.Github.Commit.ShaResponse;
import org.example.Service.LoggerService;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
public class GitHubClient {

    private final WebClient webClient;
    private final LoggerService loggerService;

    public GitHubClient(WebClient webClient, LoggerService loggerService) {
        this.webClient = webClient;
        this.loggerService = loggerService;
    }

    private ShaResponse[] getShaArrayPerPage(DatedListCommitRequest commitRequest, int page) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/commits")
                        .queryParam("page", page)
                        .queryParam("sha", commitRequest.branch())
                        .queryParam("since", commitRequest.since())
                        .queryParam("until", commitRequest.until())
                        .build(commitRequest.owner(), commitRequest.repo()))
                .retrieve()
                .bodyToMono(ShaResponse[].class)
                .block();

    }

    private CommitResponse getCommitInfo(DatedListCommitRequest commitRequest, String sha) {

        return  webClient.get()
                .uri("/repos/{owner}/{repo}/commits/{sha}", commitRequest.owner(), commitRequest.repo(), sha)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(CommitResponse.class)
                .block();
    }


    public List<CommitResponse> getInto(DatedListCommitRequest commitRequest) {
        int page = 1;
        int pageSize = 30;
        int currentPageSize = 0;

        List<CompletableFuture<CommitResponse>> futures = new ArrayList<>();

        do {

            ShaResponse[] shaArray = getShaArrayPerPage(commitRequest, page);

            if (shaArray != null && shaArray.length > 0) {
                currentPageSize = shaArray.length;
                page = page + 1;

                Arrays.stream(shaArray).forEach(shaResponse -> {
                    CompletableFuture<CommitResponse> future = CompletableFuture.supplyAsync(() ->
                            getCommitInfo(commitRequest, shaResponse.sha())
                    );
                    futures.add(future);
                });
            }

        } while (currentPageSize == pageSize);

        List<CommitResponse> result = futures.stream()
                .map(CompletableFuture::join)
                .toList();


        loggerService.log(result);

        return result;
    }


}
