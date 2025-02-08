package org.example.Repository;

import org.example.Entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FileRepo extends JpaRepository<FileEntity, Long> {

    @Transactional
    Optional<FileEntity> findByName(String fileName);

    @Modifying
    @Transactional
    @Query("UPDATE FileEntity b SET b.name = :newName WHERE b.name = :oldName")
    void updateName(@Param("newName") String newName, @Param("oldName") String oldName);
}
