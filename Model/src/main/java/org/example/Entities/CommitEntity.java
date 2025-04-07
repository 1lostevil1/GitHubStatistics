package org.example.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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

    @Column(name="author")
    private String author;

    @Column(name="date")
    private OffsetDateTime date;

    private int additions;

    private int deletions;


    private int changes;

    @Column(name = "current_name")
    String currentName;

    @Column(name = "change_sha")
    String changeSha;

    @Column(name = "previous_names")
    String previousNames;


    String state;

    public CommitEntity(FileEntity file, BranchEntity branch,String author,OffsetDateTime date, int additions, int deletions, int changes,String currentName,String changeSha,String previousNames,String state) {
        this.file = file;
        this.branch = branch;
        this.author = author;
        this.date = date;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = changes;
        this.currentName = currentName;
        this.changeSha = changeSha;
        this.previousNames = previousNames;
        this.state = state;

    }


}
