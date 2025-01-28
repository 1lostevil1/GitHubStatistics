CREATE TABLE Branch (
                        id SERIAL PRIMARY KEY,
                        owner VARCHAR(255) NOT NULL,
                        repo VARCHAR(255) NOT NULL,
                        branch_name VARCHAR(255) NOT NULL,
                        check_at TIMESTAMP NOT NULL
);