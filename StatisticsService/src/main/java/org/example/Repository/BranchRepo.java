package org.example.Repository;

import org.example.Entities.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepo extends JpaRepository<BranchEntity, Long> {

 @Modifying
 @Transactional
 @Query("UPDATE BranchEntity b SET b.checkAt= :timestamp WHERE b.url = :url")
 void updateCheckAtByUrl(
         @Param("timestamp") OffsetDateTime timestamp,
         @Param("url") String url

 );

 List<BranchEntity> findAllByCheckAtBefore(OffsetDateTime checkAt);

 Optional<BranchEntity> findByUrl(String url);

}
