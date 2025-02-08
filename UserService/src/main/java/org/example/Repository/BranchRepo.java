package org.example.Repository;

import org.example.Entities.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BranchRepo extends JpaRepository<BranchEntity, Long> {

    @Transactional
    Optional<BranchEntity> findByUrl(String url);
}
