-- Creating the 'users' table
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_type VARCHAR(255) NOT NULL,
    is_account_non_expired BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN DEFAULT TRUE,
    is_enabled BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    face_id_token VARCHAR(255)
);

-- Creating the 'roles' table
CREATE TABLE roles (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    authority VARCHAR(255) UNIQUE NOT NULL
);

-- Creating the 'user_roles' junction table
CREATE TABLE user_roles (
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Creating the 'tokens' table
CREATE TABLE tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    token_type VARCHAR(255) NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    expired BOOLEAN DEFAULT FALSE,
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Creating the 'product' table
CREATE TABLE product (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    rating DOUBLE PRECISION DEFAULT 0,  -- Changed to DOUBLE PRECISION for 'double' type
    discount DOUBLE PRECISION DEFAULT 0, -- Changed to DOUBLE PRECISION for 'double' type
    image_url VARCHAR(255),
    total_sold INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Creating the 'product_variants' table
CREATE TABLE product_variants (
    id UUID PRIMARY KEY,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    color VARCHAR(100),
    size VARCHAR(50),
    stock INT NOT NULL
);

-- Creating the 'carts' table
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(19, 2) DEFAULT 0
);

-- Creating the 'cart_items' table
CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID REFERENCES carts(id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    product_variant_id UUID REFERENCES product_variants(id) ON DELETE CASCADE,
    quantity INT NOT NULL,
    unit_price_snapshot DECIMAL(19, 2),
    list_price_snapshot DECIMAL(19, 2),
    discount_percent_snapshot DECIMAL(5, 2),
    currency VARCHAR(10) DEFAULT 'NGN',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Creating the 'orders' table
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    amount DECIMAL(19, 2) DEFAULT 0,
    cart_id UUID REFERENCES carts(id) ON DELETE CASCADE,
    order_status VARCHAR(255) NOT NULL,
    confirmation_code VARCHAR(255),
    placed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Creating the 'order_items' table
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    product_variant_id UUID REFERENCES product_variants(id) ON DELETE CASCADE,
    quantity INT NOT NULL,
    unit_price DECIMAL(19, 2),
    total_price DECIMAL(19, 2)
);

-- Creating the 'payment_transactions' table
CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    user_id UUID NOT NULL,
    payment_reference VARCHAR(255) NOT NULL UNIQUE,
    transaction_reference VARCHAR(255),
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- Creating the 'otp' table (for OTP functionality)
CREATE TABLE otp (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    token VARCHAR(255) DEFAULT '',
    expired BOOLEAN DEFAULT FALSE,
    expiry_time TIMESTAMP WITH TIME ZONE,
    email VARCHAR(255) DEFAULT '',
    otp_event VARCHAR(255) DEFAULT 'NONE'
);
