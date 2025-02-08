package org.example.Repository;

import org.example.Entities.BranchEntity;
import org.example.Entities.CommitEntity;
import org.example.Entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommitRepo extends JpaRepository<CommitEntity, Long> {

    boolean existsByBranchAndFile(BranchEntity branch, FileEntity file);

    Optional<CommitEntity> findByBranchAndFile(BranchEntity branch, FileEntity file);

    List<CommitEntity> findByBranch(BranchEntity branch);

//
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM CommitEntity c WHERE c.branch = :branch AND c.file = :file")
//    void deleteByBranchAndFile(@Param("branch") BranchEntity branch, @Param("file") FileEntity file);
}
