CREATE TABLE File_Branch (
                             file_id BIGINT NOT NULL,
                             branch_id BIGINT NOT NULL,
                             PRIMARY KEY (file_id, branch_id),
                             FOREIGN KEY (file_id) REFERENCES File(id) ON DELETE CASCADE,
                             FOREIGN KEY (branch_id) REFERENCES Branch(id) ON DELETE CASCADE
);