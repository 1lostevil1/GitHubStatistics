package org.example.Service;

import org.example.DTO.UserDTO;
import org.example.Entities.User.UserEntity;
import org.example.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    public UserDTO login(String username,String email, int passwordHash) {
        userRepo.saveAndFlush(new UserEntity(username,email,passwordHash));
        return new UserDTO(username, email, passwordHash);
    }

    public UserDTO getInfo(String email){
        UserEntity userEntity = userRepo.findByEmail(email);
        return new UserDTO(userEntity.getUsername(),userEntity.getEmail(),userEntity.getPasswordHash());
    }
}
