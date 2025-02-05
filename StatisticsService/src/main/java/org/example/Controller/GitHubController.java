package org.example.Controller;


import lombok.extern.slf4j.Slf4j;
import org.example.DTO.BranchDTO;
import org.example.Entities.Github.BranchEntity;
import org.example.Repository.BranchRepo;
import org.example.Request.User.SubscriptionRequest;
import org.example.Service.BranchUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/stat")
@Slf4j
public class GitHubController {

    @Autowired
    BranchUpdateService branchUpdateService;

    @PostMapping("/recentlyAdded")
    public  void recentlyAddedCheck(@RequestBody SubscriptionRequest subscriptionRequest) {

        log.info("recentlyAddedCheck {}", subscriptionRequest);
    branchUpdateService.checkUpdates(List.of(new BranchDTO(
            subscriptionRequest.owner(),
            subscriptionRequest.repo(),
            subscriptionRequest.branchName(),
            OffsetDateTime.now().minusYears(30)))
    );

    }
}
