package org.example.Controller;

import org.example.Client.GitHubClient;
import org.example.Response.CommitResponse;
import org.example.Response.ShaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    private GitHubClient gitHubClient;

    @GetMapping("/subscribe")
    public List<CommitResponse> getInto() {
        return  gitHubClient.getInto("1lostevil1", "javaCourse_TelegramBot");
    }
}
