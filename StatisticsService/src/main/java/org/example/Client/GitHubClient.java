package org.example.Client;

import org.example.Config.ApplicationConfig;
import org.example.Response.CommitResponse;
import org.example.Response.ShaResponse;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.stream.Stream;


public class GitHubClient {

    private final WebClient webClient;
    private final String apiToken;

    public GitHubClient(WebClient webClient, ApplicationConfig config) {
        this.apiToken = config.gitHubToken();
        this.webClient = webClient;
    }

    private ShaResponse[] getShaArrayPerPage(String owner, String repo, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/commits")
                        .queryParam("page", page)
//                        .queryParam("since", since)
//                        .queryParam("until", until)
                        .build(owner, repo))
                .header("Authorization", "token " + apiToken)
                .retrieve()
                .bodyToMono(ShaResponse[].class)
                .block();

    }

    private CommitResponse getCommitInfo(String owner, String repo, String sha) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/commits/{sha}", owner, repo, sha)
                .header("Authorization", "token " + apiToken)
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
