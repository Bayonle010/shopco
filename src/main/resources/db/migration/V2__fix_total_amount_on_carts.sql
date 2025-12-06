-- Set 0 for existing null rows
UPDATE carts
SET total_amount = 0
WHERE total_amount IS NULL;

-- Set default 0 for new rows
ALTER TABLE carts
    ALTER COLUMN total_amount SET DEFAULT 0;

-- Enforce NOT NULL constraint
ALTER TABLE carts
    ALTER COLUMN total_amount SET NOT NULL;
