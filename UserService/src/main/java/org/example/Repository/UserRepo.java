package org.example.Repository;

import org.example.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    @Transactional
    Optional<UserEntity> findByEmail(String email);

    @Transactional
    Optional<UserEntity> findByUsername(String username);

}
