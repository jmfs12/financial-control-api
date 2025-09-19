CREATE TABLE account(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGSERIAL NOT NULL,
    name VARCHAR(20) NOT NULL,
    type VARCHAR(10) NOT NULL CHECK(type IN ('checking', 'savings', 'credit')),
    currency VARCHAR(3) NOT NULL,
    balance_snapshot DECIMAL (19,4) NOT NULL DEFAULT 0.00,
    institution VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
);