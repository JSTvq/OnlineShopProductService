CREATE TABLE IF NOT EXISTS outbox_product (
    id SERIAL PRIMARY KEY,
    aggregate_type VARCHAR(255),
    aggregate_id BIGINT,
    topic VARCHAR(255),
    type VARCHAR(255),
    payload JSONB,
    status VARCHAR(50),
    created_at timestamp,
    updated_at timestamp
);