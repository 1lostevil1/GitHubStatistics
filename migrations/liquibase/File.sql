CREATE TABLE File (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      additions INT NOT NULL,
                      deletions INT NOT NULL,
                      changes INT NOT NULL
);