package org.example.Controller;


import lombok.extern.slf4j.Slf4j;
import org.example.DTO.BranchDTO;
import org.example.Request.User.SubscriptionRequest;
import org.example.Service.BranchUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/stat")
@Slf4j
public class UpdateController {


    @Autowired
    BranchUpdateService branchUpdateService;

    @PostMapping("/recentlyAdded")
    public  void recentlyAddedCheck(@RequestBody SubscriptionRequest subscriptionRequest) {

        log.info("recentlyAddedCheck {}", subscriptionRequest);

        LocalDateTime dateTime = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.ofHours(3));

        branchUpdateService.initialSub(new BranchDTO(subscriptionRequest.url(),offsetDateTime));

    }
}
