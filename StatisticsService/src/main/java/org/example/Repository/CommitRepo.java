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

    Optional<CommitEntity> findByCurrentNameAndBranch(String name, BranchEntity branch );

    List<CommitEntity> findAllByCurrentNameAndBranch(String name, BranchEntity branch );

    Optional<CommitEntity> findByCurrentNameAndBranchAndChangeSha(String currentName,BranchEntity branch, String changeSha);

    List<CommitEntity> findAllByCurrentNameAndBranchAndAddedStringsContaining(String name, BranchEntity branch,String string);

    List<CommitEntity> findAllByCurrentNameAndBranchAndDeletedStringsContaining(String name, BranchEntity branch,String string);

}
