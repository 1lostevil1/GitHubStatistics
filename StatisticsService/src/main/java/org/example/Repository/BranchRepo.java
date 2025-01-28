package org.example.Repository;

import org.example.Entities.Github.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Repository
public interface BranchRepo extends JpaRepository<BranchEntity, Long> {

 @Modifying
 @Transactional
 @Query("UPDATE BranchEntity b SET b.check_at = :checkAt WHERE b.owner = :owner AND b.repo = :repo AND b.branch_name = :branchName")
 int updateCheckAtByOwnerRepoAndBranchName(
         @Param("checkAt") OffsetDateTime checkAt,
         @Param("owner") String owner,
         @Param("repo") String repo,
         @Param("branchName") String branchName
 );
}
