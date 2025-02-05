package org.example.Repository;

import org.example.Entities.Github.BranchEntity;
import org.example.Entities.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Repository
public interface BranchRepo extends JpaRepository<BranchEntity, Long> {

    @Transactional
    Optional<BranchEntity> findByName(String branch);
}
