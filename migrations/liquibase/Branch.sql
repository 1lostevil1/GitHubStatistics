CREATE TABLE Branch (
                        id SERIAL PRIMARY KEY,
                        url VARCHAR(255) NOT NULL UNIQUE ,
                        check_at TIMESTAMP WITH TIME ZONE NOT NULL
);