package org.example.Controller;


import lombok.extern.slf4j.Slf4j;
import org.example.DTO.BranchDTO;
import org.example.Request.User.SubscriptionRequest;
import org.example.Service.BranchUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    branchUpdateService.initialSub(new BranchDTO(
                                                 subscriptionRequest.url(),
                                                 OffsetDateTime.now().minusYears(30))
                                                );

    }
}
