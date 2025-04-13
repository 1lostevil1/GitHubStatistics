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
    @JoinColumn(name = "file_id", nullable = false)
    private FileEntity file;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branch;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "date")
    private OffsetDateTime date;

    @Column(nullable = false)
    private int additions;

    @Column(nullable = false)
    private int deletions;

    @Column(nullable = false)
    private int changes;

    @Column(nullable = false)
    private int refactors;

    @Column(name = "current_name", columnDefinition = "TEXT DEFAULT ''")
    private String currentName;

    @Column(name = "change_sha", nullable = false)
    private String changeSha;

    @Column(name = "previous_names", columnDefinition = "TEXT DEFAULT ''")
    private String previousNames;

    @Column(name = "added_strings", columnDefinition = "TEXT DEFAULT ''")
    private String addedStrings;

    @Column(name = "deleted_strings", columnDefinition = "TEXT DEFAULT ''")
    private String deletedStrings;

    @Column(length = 100)
    private String state;

    // Дополнительный удобный конструктор без id
    public CommitEntity(FileEntity file, BranchEntity branch, String author, OffsetDateTime date,
                        int additions, int deletions, int refactors, int changes,
                        String currentName, String changeSha, String previousNames,
                        String addedStrings, String deletedStrings, String state) {
        this.file = file;
        this.branch = branch;
        this.author = author;
        this.date = date;
        this.additions = additions;
        this.deletions = deletions;
        this.refactors = refactors;
        this.changes = changes;
        this.currentName = currentName;
        this.changeSha = changeSha;
        this.previousNames = previousNames;
        this.addedStrings = addedStrings;
        this.deletedStrings = deletedStrings;
        this.state = state;
    }
}
