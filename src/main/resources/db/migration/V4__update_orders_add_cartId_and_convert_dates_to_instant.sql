-- Add cart_id column
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS cart_id UUID;

--Convert placedAt to TIMESTAMP WITHOUT TIME ZONE (Instant-compatible)
ALTER TABLE orders
    ALTER COLUMN placed_at TYPE TIMESTAMP WITHOUT TIME ZONE
    USING placed_at;

-- Convert completedAt to TIMESTAMP WITHOUT TIME ZONE
ALTER TABLE orders
    ALTER COLUMN completed_at TYPE TIMESTAMP WITHOUT TIME ZONE
    USING completed_at;

-- Convert createdAt to TIMESTAMP WITHOUT TIME ZONE
ALTER TABLE orders
    ALTER COLUMN created_at TYPE TIMESTAMP WITHOUT TIME ZONE
    USING created_at;
