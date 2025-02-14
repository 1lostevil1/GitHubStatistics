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
import org.example.Request.User.UnsubscriptionRequest;
import org.example.Response.User.SubscriptionResponse;
import org.example.Response.User.UnsubscriptionResponse;
import org.example.Utils.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class SubscriptionService {

    private final BranchRepo branchRepo;

    private final UserRepo userRepo;

    private final JwtTokenUtils jwtTokenUtils;

    private final StatisticsClient statisticsClient;


    public ResponseEntity<?> subscribe(SubscriptionRequest subscriptionRequest, String token) {


        String username = jwtTokenUtils.getUsername(token.substring(7));

        Optional<UserEntity> userEntityOptional = userRepo.findByUsername(username);

        if (userEntityOptional.isEmpty()) {
            return new ResponseEntity<>(new RepeatedSubscriptionException(HttpStatus.BAD_REQUEST.value(), "Пользователь отсутствует в бд"), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userEntityOptional.get();

        Optional<BranchEntity> branchEntityOptional = branchRepo.findByUrl(subscriptionRequest.url());

        BranchEntity branchEntity;

        if (branchEntityOptional.isPresent()) {

            branchEntity = branchEntityOptional.get();

            if (userEntity.getBranches().stream().anyMatch(branch -> branch.getUrl().equals(subscriptionRequest.url()))) {

                return new ResponseEntity<>(new RepeatedSubscriptionException(HttpStatus.BAD_REQUEST.value(), "Такая ветка уже отслеживается"), HttpStatus.BAD_REQUEST);
            } else {

                branchEntity.getUsers().add(userEntity);
                branchRepo.saveAndFlush(branchEntity);

                userEntity.getBranches().add(branchEntity);
                userRepo.saveAndFlush(userEntity);


            }


        } else {


            branchEntity = new BranchEntity(subscriptionRequest.url(), OffsetDateTime.now().plusYears(30));
            branchRepo.saveAndFlush(branchEntity);

            branchEntity.getUsers().add(userEntity);
            userEntity.getBranches().add(branchEntity);
            userRepo.saveAndFlush(userEntity);
        }





        statisticsClient.sendSubscription(subscriptionRequest);

        log.info("user {} subscribed!", username);
        return ResponseEntity.ok(new SubscriptionResponse(username,subscriptionRequest.url()));


    }

    public ResponseEntity<?> unsubscribe(UnsubscriptionRequest unsubscriptionRequest, String token) {

        String username = jwtTokenUtils.getUsername(token.substring(7));

        Optional<UserEntity> userEntityOptional = userRepo.findByUsername(username);

        BranchEntity branchEntity = branchRepo.findByUrl(unsubscriptionRequest.url()).orElseThrow();

        if (userEntityOptional.isEmpty()) {
            return new ResponseEntity<>(new RepeatedSubscriptionException(HttpStatus.BAD_REQUEST.value(), "Пользователь отсутствует в бд"), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userEntityOptional.get();
        Set<BranchEntity> branches = userEntity.getBranches();

        if (branches.stream().flatMap(branch -> branch.getUsers().stream()).noneMatch(user -> user == userEntity)) {

            branchRepo.removeBranchEntityByUrl(unsubscriptionRequest.url());
        }

        userEntity.getBranches().remove(branchEntity);
        userRepo.saveAndFlush(userEntity);

        return ResponseEntity.ok(new UnsubscriptionResponse(username,unsubscriptionRequest.url()));
    }


}
