CREATE TABLE Commit (
                             id SERIAL PRIMARY KEY,
                             file_id BIGINT NOT NULL,
                             branch_id BIGINT NOT NULL,
                             additions INT NOT NULL,
                             deletions INT NOT NULL,
                             changes INT NOT NULL,
                             previous_names TEXT DEFAULT '',
                             state VARCHAR(100),
                             FOREIGN KEY (file_id) REFERENCES File(id) ON DELETE CASCADE,
                             FOREIGN KEY (branch_id) REFERENCES Branch(id) ON DELETE CASCADE
);