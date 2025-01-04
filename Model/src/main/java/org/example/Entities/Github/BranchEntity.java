package org.example.Entities.Github;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Branch")
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private OffsetDateTime updateAt;
    private OffsetDateTime checkAt;
    @ManyToMany(mappedBy = "branches")
    private Set<FileEntity> files = new HashSet<>();
}
