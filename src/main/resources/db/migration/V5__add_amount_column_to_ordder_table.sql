-- Add amount column to orders table
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS amount NUMERIC(19,2) NOT NULL DEFAULT 0;
