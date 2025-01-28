package org.example.Entities.Github;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Branch")
@NoArgsConstructor
public class BranchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String owner;
    private String repo;
    private String branch_name;
    private OffsetDateTime check_at;
    @ManyToMany(mappedBy = "branches")
    private Set<FileEntity> files = new HashSet<>();

    public BranchEntity( String owner, String repo, String branchName, OffsetDateTime checkAt) {
        this.owner = owner;
        this.repo = repo;
        this.branch_name = branchName;
        this.check_at = checkAt;
    }
}
