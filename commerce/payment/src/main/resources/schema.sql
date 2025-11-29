CREATE SCHEMA IF NOT EXISTS payments;

CREATE TABLE IF NOT EXISTS payments.payments
(
    payment_id
    UUID
    DEFAULT
    gen_random_uuid
(
) PRIMARY KEY,
    products_total NUMERIC NOT NULL,
    order_id UUID NOT NUll,
    delivery_total NUMERIC
(
    19,
    2
),
    total_payment NUMERIC
(
    19,
    2
),
    fee_total NUMERIC
(
    19,
    2
),
    status VARCHAR
(
    50
) NOT NULL DEFAULT 'PENDING' CHECK
(
    status
    IN
(
    'PENDING',
    'SUCCESS',
    'FAILED'
))
    );
