package org.example.Repository;

import org.example.Entities.Github.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BranchRepo extends JpaRepository<BranchEntity, Long> {

 @Modifying
 @Transactional
 @Query("UPDATE BranchEntity b SET b.timestamp= :timestamp WHERE b.owner = :owner AND b.repo = :repo AND b.name = :branchName")
 void updateTimestampByOwnerRepoAndBranchName(
         @Param("timestamp") OffsetDateTime timestamp,
         @Param("owner") String owner,
         @Param("repo") String repo,
         @Param("branchName") String branchName
 );

 List<BranchEntity> findAllByTimestampBefore(OffsetDateTime timestamp);

}
