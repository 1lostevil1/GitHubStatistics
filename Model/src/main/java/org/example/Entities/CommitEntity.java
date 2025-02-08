package org.example.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "commit")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileEntity file;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    private int additions;

    private int deletions;


    private int changes;
    @Column(name = "previous_names")
    String previousNames;

    String state;

    public CommitEntity(FileEntity file, BranchEntity branch, int additions, int deletions, int changes,String previousNames,String state) {
        this.file = file;
        this.branch = branch;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = changes;
        this.previousNames = previousNames;
        this.state = state;

    }


}
