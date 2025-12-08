CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    user_id UUID NOT NULL,
    payment_reference VARCHAR(255) NOT NULL UNIQUE,
    transaction_reference VARCHAR(255),
    amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
