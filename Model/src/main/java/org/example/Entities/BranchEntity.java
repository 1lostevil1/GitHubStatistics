package org.example.Entities;

import jakarta.persistence.*;
import lombok.*;

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
    private String url;
    @Column(name = "check_at")
    private OffsetDateTime checkAt;


    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommitEntity> commits;

    @ManyToMany
    @JoinTable(
            name = "user_branch",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id")
    )
    private Set<UserEntity> users = new HashSet<>();



    public BranchEntity( String url, OffsetDateTime checkAt) {
        this.url = url;
        this.checkAt = checkAt;
    }

    public BranchEntity( String url,OffsetDateTime checkAt, Set<UserEntity> users) {
        this.url = url;
        this.checkAt = checkAt;
        this.users = users;
    }
}
