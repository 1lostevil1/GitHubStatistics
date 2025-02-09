package org.example.Controller.Websocket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Request.Github.UpdateRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

        log.info("topic:::  /topic/{}", request.topic());
        log.info("request:::    {}", request);

      messagingTemplate.convertAndSend("/topic/" + request.topic(), request);


    }
}


