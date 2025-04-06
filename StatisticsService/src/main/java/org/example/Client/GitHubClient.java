package org.example.Client;

import lombok.extern.slf4j.Slf4j;
import org.example.Request.Github.DatedListCommitRequest;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.ShaResponse;
import org.example.Service.LoggerService;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
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

        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits/{sha}", commitRequest.owner(), commitRequest.repo(), sha)
                .retrieve()
                .bodyToMono(CommitResponse.class)
                .block();
    }


    public List<CommitResponse> getInto(DatedListCommitRequest commitRequest) {
        int page = 1;
        int pageSize = 30;
        int currentPageSize = 0;

        List<CommitResponse> result = new ArrayList<>();

        do {
            ShaResponse[] shaArray = getShaArrayPerPage(commitRequest, page);
            if (shaArray != null && shaArray.length > 0) {
                currentPageSize = shaArray.length;
                page = page + 1;
                result.addAll(Stream.of(shaArray)
                        .map(shaResponse -> getCommitInfo(commitRequest, shaResponse.sha()))
                        .toList());

            }

        } while (currentPageSize == pageSize);

        loggerService.log(result);

        return result;
    }


}
