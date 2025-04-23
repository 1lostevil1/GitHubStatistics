package org.example.Service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Entities.BranchEntity;
import org.example.Entities.UserEntity;
import org.example.Repository.BranchRepo;
import org.example.Repository.UserRepo;
import org.example.Request.User.CurrentUpdateRequest;
import org.example.Request.User.UpdateRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DistributionService {


    private final BranchRepo branchRepo;
    private final UserRepo userRepo;
    private SimpMessagingTemplate messagingTemplate;

    public void sendUpdate(UpdateRequest updateRequest) {

        BranchEntity branchEntity = branchRepo.findByUrl(updateRequest.branch()).orElseThrow();
        log.info("sent {} update to frontend", updateRequest.branch());
        log.info(branchEntity.getUsers().toString());

        branchEntity.getUsers().forEach(
                user -> {
                    send(user, updateRequest);
                    log.info(user.toString());
                }
        );
    }

    public void sendCurrentUpdate(CurrentUpdateRequest currentUpdateRequest) {

        BranchEntity branchEntity = branchRepo.findByUrl(currentUpdateRequest.updateRequest().branch()).orElseThrow();
        UserEntity user = userRepo.findByUsername(currentUpdateRequest.username()).orElseThrow();
        log.info("sent {} update to frontend", currentUpdateRequest.updateRequest().branch());
        log.info(branchEntity.getUsers().toString());
        send(user, currentUpdateRequest.updateRequest());
        log.info(user.toString());
    }

    private void send(UserEntity user , UpdateRequest updateRequest) {
        String topic = "/topic/messages/"+user.getUsername();
        messagingTemplate.convertAndSend(topic, updateRequest);
    }


}
