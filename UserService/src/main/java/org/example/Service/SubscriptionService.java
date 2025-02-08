package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Client.StatisticsClient;
import org.example.Entities.BranchEntity;
import org.example.Entities.UserEntity;
import org.example.Exception.RepeatedSubscriptionException;
import org.example.Repository.BranchRepo;
import org.example.Repository.UserRepo;
import org.example.Request.User.SubscriptionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SubscriptionService {

    private final BranchRepo branchRepo;

    private final UserRepo userRepo;

    private final StatisticsClient statisticsClient;


    public ResponseEntity<?> subscribe(SubscriptionRequest subscriptionRequest) {

        Optional<UserEntity> userEntityOptional = userRepo.findByUsername(subscriptionRequest.username());

        if(userEntityOptional.isEmpty()) {
            return new ResponseEntity<>(new RepeatedSubscriptionException(HttpStatus.BAD_REQUEST.value(), "Пользователь отсутствует в бд"), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userEntityOptional.get();

        Optional<BranchEntity> branchEntityOptional = branchRepo.findByUrl(subscriptionRequest.url());

        if (branchEntityOptional.isPresent()) {


                if (userEntity.getBranches().stream().anyMatch(branch -> branch.getUrl().equals(subscriptionRequest.url()))) {

                    return new ResponseEntity<>(new RepeatedSubscriptionException(HttpStatus.BAD_REQUEST.value(), "Такая ветка уже отслеживается"), HttpStatus.BAD_REQUEST);
                }

                userEntity.getBranches().add(branchEntityOptional.get());


        } else {
            //TODO Добавлять в бд уже после лучше наверное
            BranchEntity branchEntity = new BranchEntity(subscriptionRequest.url(), OffsetDateTime.now().minusYears(30));
            userEntity.getBranches().add(branchEntity);
            branchRepo.saveAndFlush(branchEntity);
            log.info("sended sub info TO STAT SERVICE");
            statisticsClient.sendSubscription(subscriptionRequest);

        }


        userRepo.saveAndFlush(userEntity);
        return  ResponseEntity.ok(subscriptionRequest);


    }

}
