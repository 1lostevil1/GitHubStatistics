package org.example.Client;

import org.example.Response.Commit.CommitResponse;
import org.example.Response.ShaResponse;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.stream.Stream;


public class GitHubClient {

    private final WebClient webClient;

    public GitHubClient(WebClient webClient) {
        this.webClient = webClient;
    }

    private ShaResponse[] getShaArrayPerPage(String owner, String repo, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/commits")
                        .queryParam("page", page)
//                        .queryParam("since", since)
//                        .queryParam("until", until)
                        .build(owner, repo))
                .retrieve()
                .bodyToMono(ShaResponse[].class)
                .block();

    }

    private CommitResponse getCommitInfo(String owner, String repo, String sha) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits/{sha}", owner, repo, sha)
                .retrieve()
                .bodyToMono(CommitResponse.class)
                .block();
    }


    public List<CommitResponse> getInto(String owner, String repo) {
        int page = 1;
        int pageSize = 30;
        List<CommitResponse> result = new ArrayList<>();
        List<CommitResponse> perPage =
                Stream.of(getShaArrayPerPage(owner, repo, page))
                        .map(shaResponse -> getCommitInfo(owner, repo, shaResponse.sha()))
                        .toList();
        do {
            System.out.println(perPage);
            System.out.println(page);
            result.addAll(perPage);
            perPage =
                    Stream.of(getShaArrayPerPage(owner, repo, ++page))
                            .map(shaResponse -> getCommitInfo(owner, repo, shaResponse.sha()))
                            .toList();
        } while (perPage.size() == pageSize);

        return result;
    }


}
