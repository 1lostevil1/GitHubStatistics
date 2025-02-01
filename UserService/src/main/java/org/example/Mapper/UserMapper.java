package org.example.Mapper;

import org.example.DTO.UserDTO;
import org.example.Entities.User.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO UserEntityToUserDTO(UserEntity userEntity) {
        return new UserDTO(userEntity.getUsername(), userEntity.getEmail(),userEntity.getPassword());
    }

    public UserEntity UserDTOToUserEntity(UserDTO userDTO) {
        return new UserEntity(userDTO.username(),userDTO.email(),userDTO.password());
    }
}
