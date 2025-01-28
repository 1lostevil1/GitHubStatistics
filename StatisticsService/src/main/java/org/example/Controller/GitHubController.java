package org.example.Controller;


import org.example.Entities.Github.BranchEntity;
import org.example.Repository.BranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/git")
public class GitHubController {

    @Autowired
    BranchRepo branchRepo;

    @GetMapping
    public  void add(@RequestParam String owner, @RequestParam String repo, @RequestParam String branch) {
        branchRepo.saveAndFlush(new BranchEntity(owner,repo,branch, OffsetDateTime.now().minusYears(20)));
    }
}
