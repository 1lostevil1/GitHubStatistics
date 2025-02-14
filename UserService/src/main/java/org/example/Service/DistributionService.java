package org.example.Service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Entities.BranchEntity;
import org.example.Repository.BranchRepo;
import org.example.Request.User.UpdateRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DistributionService {


    private final BranchRepo branchRepo;

    private SimpMessagingTemplate messagingTemplate;

    public void sendUpdate(UpdateRequest updateRequest) {

        BranchEntity branchEntity = branchRepo.findByUrl(updateRequest.branch()).orElseThrow();

        log.info("sent {} update to frontend", updateRequest.branch());

        log.info(branchEntity.getUsers().toString());

        branchEntity.getUsers().forEach(
                user -> {
                    String topic = "/topic/messages/"+user.getUsername();
                    messagingTemplate.convertAndSend(topic, updateRequest);
                    log.info(user.toString());
                    log.info(topic);
                }
        );


    }
}
