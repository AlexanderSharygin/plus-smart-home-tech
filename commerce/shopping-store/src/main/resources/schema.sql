CREATE SCHEMA IF NOT EXISTS store;

CREATE TABLE IF NOT EXISTS store.products
(
    product_id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(512),
    quantity_state VARCHAR(50) NOT NULL CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    product_state VARCHAR(50) NOT NULL CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    rating  NUMERIC(2,1),
    product_category VARCHAR(50),
    price NUMERIC(19, 2) NOT NULL CHECK (price >= 1)
);
