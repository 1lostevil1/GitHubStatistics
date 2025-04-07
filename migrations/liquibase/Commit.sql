CREATE TABLE Commit (
                             id SERIAL PRIMARY KEY,
                             file_id BIGINT NOT NULL,
                             branch_id BIGINT NOT NULL,
                             author VARCHAR(255) NOT NULL,
                             date TIMESTAMP WITH TIME ZONE,
                             additions INT NOT NULL,
                             deletions INT NOT NULL,
                             changes INT NOT NULL,
                             change_sha VARCHAR(255) NOT NULL,
                             current_name TEXT DEFAULT '',
                             previous_names TEXT DEFAULT '',
                             state VARCHAR(100),
                             FOREIGN KEY (file_id) REFERENCES File(id) ON DELETE CASCADE,
                             FOREIGN KEY (branch_id) REFERENCES Branch(id) ON DELETE CASCADE
);