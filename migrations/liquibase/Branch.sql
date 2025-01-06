CREATE TABLE Branch (
                        id SERIAL PRIMARY KEY,
                        url VARCHAR(255) NOT NULL,
                        updateAt TIMESTAMP NOT NULL,
                        checkAt TIMESTAMP NOT NULL
);