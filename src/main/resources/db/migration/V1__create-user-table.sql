CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK ( role IN ('USER', 'ADMIN') ),
    created_at TIMESTAMP default CURRENT_TIMESTAMP
);


