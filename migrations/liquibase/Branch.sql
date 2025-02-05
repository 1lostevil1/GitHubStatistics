CREATE TABLE Branch (
                        id SERIAL PRIMARY KEY,
                        owner VARCHAR(255) NOT NULL,
                        repo VARCHAR(255) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);