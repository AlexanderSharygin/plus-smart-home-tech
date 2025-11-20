CREATE SCHEMA IF NOT EXISTS cart;

CREATE TABLE IF NOT EXISTS cart.cart
(
    cart_id  UUID                  DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    active   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS cart.cart_products
(
    product_id UUID   NOT NULL,
    cart_id    UUID   NOT NULL,
    quantity   BIGINT NOT NULL,
    PRIMARY KEY (cart_id, product_id),
    FOREIGN KEY (cart_id) REFERENCES cart.cart (cart_id) ON DELETE CASCADE
);
