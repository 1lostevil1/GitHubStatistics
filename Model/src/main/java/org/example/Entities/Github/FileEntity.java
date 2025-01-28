package org.example.Entities.Github;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "File")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int additions;
    private int deletions;
    private int changes;
    @ManyToMany
    @JoinTable(
                name = "commit",
                joinColumns = @JoinColumn(name = "file_id"),
                inverseJoinColumns = @JoinColumn(name = "branch_id")
              )
    private Set<BranchEntity> branches;
}
