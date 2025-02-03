CREATE TABLE Branch (
                        id SERIAL PRIMARY KEY,
                        owner VARCHAR(255) NOT NULL,
                        repo VARCHAR(255) NOT NULL,
                        branch VARCHAR(255) NOT NULL,
                        timestamp TIMESTAMP NOT NULL
);