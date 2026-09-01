CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_email UNIQUE (email)
);

CREATE TABLE wallet (
    wallet_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    balance DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (wallet_id),
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE transaction (
    transaction_id BIGINT NOT NULL AUTO_INCREMENT,
    transaction_type VARCHAR(255) NOT NULL,
    transaction_state VARCHAR(255) NOT NULL,
    wallet_id BIGINT NOT NULL,
    timestamp DATETIME NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (transaction_id),
    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallet(wallet_id)
);