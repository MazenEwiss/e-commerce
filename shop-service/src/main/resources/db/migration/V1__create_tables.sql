CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(19,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    order_date DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE order_item (
    order_item_id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price_at_purchase DECIMAL(19,2) NOT NULL,
    order_id BIGINT,
    PRIMARY KEY (order_item_id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE payment (
    payment_id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    payment_date DATETIME NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    wallet_transaction_id BIGINT,
    PRIMARY KEY (payment_id),
    CONSTRAINT uk_payment_order UNIQUE (order_id),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE cart (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cart_item (
    cart_item_id BIGINT NOT NULL AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    cart_id BIGINT,
    PRIMARY KEY (cart_item_id),
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart(id)
);