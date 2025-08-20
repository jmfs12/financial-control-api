CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK ( role IN ('USER', 'ADMIN') ),
    created_at TIMESTAMP default CURRENT_TIMESTAMP
);

CREATE TABLE accounts(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK ( type IN ('checking', 'savings', 'credit') ),
    currency CHAR(3) NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),
    balance NUMERIC(18, 2) DEFAULT 0,
    institution VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE category(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    parent_id BIGINT NULL,
    type VARCHAR(20) NOT NULL CHECK ( type IN ('income', 'expense') ),
    color VARCHAR(7) NOT NULL, -- hex color #FF0000

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES category (id) ON DELETE CASCADE
);

CREATE TABLE transaction(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK ( type IN ('income', 'expense', 'transfer') ),
    amount NUMERIC(18,2) NOT NULL,
    currency CHAR(3) NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),
    description TEXT,
    tags TEXT[], -- array of tags
    posted_at TIMESTAMP NOT NULL,
    cleared_at TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'cleared')),
    external_id VARCHAR(255) NULL,
    idempotency_key VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL
);

CREATE TABLE recurring_rules (
     id BIGSERIAL PRIMARY KEY,
     user_id BIGINT NOT NULL,
     schedule TEXT NOT NULL, -- cron expression or RRULE string
     next_run_at TIMESTAMP NOT NULL,
     template JSONB NOT NULL, -- stores transaction fields like account_id, category_id, amount, currency, description
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE budget(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    month DATE NOT NULL CHECK (EXTRACT(DAY FROM month) = 1),
    limit_amount NUMERIC(18,2) DEFAULT 0,
    currency CHAR(3) NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);


