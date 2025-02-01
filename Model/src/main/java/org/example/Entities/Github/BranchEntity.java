package org.example.Entities.Github;

import jakarta.persistence.*;
import lombok.*;
import org.example.Entities.User.UserEntity;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Branch")
@AllArgsConstructor
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

    @ManyToMany
    @JoinTable(
            name = "user_branch",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id")
    )
    private Set<UserEntity> users = new HashSet<>();



    public BranchEntity( String owner, String repo, String branchName, OffsetDateTime checkAt) {
        this.owner = owner;
        this.repo = repo;
        this.branch_name = branchName;
        this.check_at = checkAt;
    }

    public BranchEntity( String owner, String repo, String branchName, OffsetDateTime checkAt, Set<UserEntity> users) {
        this.owner = owner;
        this.repo = repo;
        this.branch_name = branchName;
        this.check_at = checkAt;
        this.users = users;
    }
}
