package org.example.Service;

import lombok.AllArgsConstructor;
import org.example.DTO.SubscriptionDTO;
import org.example.Entities.Github.BranchEntity;
import org.example.Entities.User.UserEntity;
import org.example.Exception.RepeatedSubscriptionException;
import org.example.Repository.BranchRepo;
import org.example.Repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private final BranchRepo branchRepo;
    private final UserRepo userRepo;

    public ResponseEntity<?> subscribe(SubscriptionDTO subscriptionDTO) {

        UserEntity userEntity = userRepo.findByUsername(subscriptionDTO.username()).orElseThrow();

        Optional<BranchEntity> branchEntityOptional = branchRepo.findByBranch(subscriptionDTO.branchName());

        if (branchEntityOptional.isPresent()) {


                if (userEntity.getBranches().stream().anyMatch(branch -> branch.getBranch().equals(subscriptionDTO.branchName()))) {

                    return new ResponseEntity<>(new RepeatedSubscriptionException(HttpStatus.BAD_REQUEST.value(), "Такая ветка уже отслеживается"), HttpStatus.BAD_REQUEST);
                }

                userEntity.getBranches().add(branchEntityOptional.get());


        } else {

            BranchEntity branchEntity = new BranchEntity(subscriptionDTO.owner(), subscriptionDTO.repo(), subscriptionDTO.branchName(), null);
            userEntity.getBranches().add(branchEntity);
            branchRepo.saveAndFlush(branchEntity);
        }


        userRepo.saveAndFlush(userEntity);
        return  ResponseEntity.ok(subscriptionDTO);


    }

}
