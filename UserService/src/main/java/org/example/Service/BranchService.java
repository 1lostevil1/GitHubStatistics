package org.example.Service;

import lombok.AllArgsConstructor;
import org.example.DTO.BranchDTO;
import org.example.DTO.SubscriptionDTO;
import org.example.Entities.Github.BranchEntity;
import org.example.Entities.User.UserEntity;
import org.example.Exception.RepeatedSubscriptionException;
import org.example.Mapper.UserMapper;
import org.example.Repository.BranchRepo;
import org.example.Mapper.BranchMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@AllArgsConstructor
public class BranchService {

    private final BranchRepo branchRepo;
    private final BranchMapper branchMapper;
    private final UserMapper userMapper;

    public ResponseEntity<?> subscribe(SubscriptionDTO subscriptionDTO) {

//        if(branchRepo.findByBranch_name(subscriptionDTO.branchName()).isPresent()){
//            return new ResponseEntity<>(new RepeatedSubscriptionException(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
//        }

        BranchEntity entity = new BranchEntity(subscriptionDTO.owner(),subscriptionDTO.repo(),subscriptionDTO.branchName(), null, new HashSet<UserEntity>());
        branchRepo.saveAndFlush();
    }

}
