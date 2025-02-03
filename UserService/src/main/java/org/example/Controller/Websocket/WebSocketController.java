package org.example.Controller.Websocket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.SubscriptionDTO;
import org.example.Request.User.SubscriptionRequest;
import org.example.Request.Github.UpdateRequest;
import org.example.Service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Slf4j
public class WebSocketController {


    private SimpMessagingTemplate messagingTemplate;



    @PostMapping("/branches/updates")
    public void updateBranches(@RequestBody UpdateRequest request) {
        request.branches().forEach(branch -> {
            messagingTemplate.convertAndSend("/topic/branch/" + branch,
                    branch);
        });
    }


//
//    @PostMapping("/secured/subscribe")
//    public ResponseEntity<?> subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {
//
//        SubscriptionDTO subscriptionDTO = new SubscriptionDTO(
//                subscriptionRequest.username(),
//                subscriptionRequest.owner(),
//                subscriptionRequest.repo(),
//                subscriptionRequest.branchName());
//
//        return subscriptionService.subscribe(subscriptionDTO);
//
//    }

    @Scheduled(fixedRate = 5000)  // 5000 milliseconds = 5 seconds
    public void sendPeriodicUpdates() {

        messagingTemplate.convertAndSend("/topic/all", "Periodic update message sent at " + System.currentTimeMillis());
        log.info("pong");
    }
}

