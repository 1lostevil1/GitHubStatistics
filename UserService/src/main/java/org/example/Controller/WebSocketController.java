package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Request.User.SubscriptionRequest;
import org.example.Request.Github.UpdateRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class WebSocketController {


    private SimpMessagingTemplate messagingTemplate;


    @PostMapping("/branches/updates")
    public void updateBranches(@RequestBody UpdateRequest request) {
        request.branches().forEach(branch -> {
            messagingTemplate.convertAndSend("/topic/branch/" + branch,
                    request);
        });
    }

    @PostMapping("/secured/subscribe")
    public void subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {

    }
}
