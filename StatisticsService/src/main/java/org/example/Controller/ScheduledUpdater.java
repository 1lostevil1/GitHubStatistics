package org.example.Controller;
import org.example.Client.GitHubClient;
import org.example.DTO.BranchDTO;
import org.example.DTO.DatedListCommitRequest;
import org.example.Repository.BranchRepo;
import org.example.Response.Github.Commit.CommitResponse;
import org.example.Response.Github.Commit.UpdateResponse;
import org.example.Utils.Mapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@EnableScheduling
@Service
public class ScheduledUpdater {


    private final GitHubClient gitHubClient;

    private final BranchRepo branchRepo;

    private final Mapper mapper;

    private final ExecutorService executorService;

    public ScheduledUpdater(GitHubClient gitHubClient, BranchRepo branchRepo) {

        this.gitHubClient = gitHubClient;
        this.branchRepo = branchRepo;
        this.mapper = new Mapper();
        this.executorService = Executors.newFixedThreadPool(10);

    }

    @Scheduled(fixedDelayString = "#{scheduler.interval}")
    public void task(){

        List<BranchDTO>  links = branchRepo.findAll().stream().map(mapper::branchEntityToDTO).toList();

        for (BranchDTO link : links) {

            executorService.submit(() -> {


                    DatedListCommitRequest datedListCommitRequest = mapper.branchDTOtoDatedListCommitRequest(link);
                    List<CommitResponse> commitResponses = gitHubClient.getInto(datedListCommitRequest);

                    if (!commitResponses.isEmpty()) {
                        UpdateResponse updateResponse = mapper.commitListToUpdateResponse(link, commitResponses);
                        System.out.println(updateResponse);
                    }


                    branchRepo.updateCheckAtByOwnerRepoAndBranchName(
                            link.checkAt().minusMonths(1),
                            link.owner(),
                            link.repo(),
                            link.branchName()
                    );

            });
        }

        //берем из бд дату последней проверки
        //делаем запрос since=последняя дата + что=то until = сейчас
        //обновляем инфу в списке изменений файлов
        // фронтент там как-то это читает
        //дата в оффсете
    }
}
