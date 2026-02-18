-- V1__init_schema.sql

-- 1. Create Users Table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL, -- Enum: ADMIN, STAFF
                       failed_attempts INT NOT NULL DEFAULT 0,
                       account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
                       lockout_time DATETIME(6) DEFAULT NULL
);

-- 2. Create Menu Items Table
CREATE TABLE menu_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            description VARCHAR(255),
                            price BIGINT NOT NULL,
                            available BOOLEAN DEFAULT TRUE,
                            created_at DATETIME(6),
                            updated_at DATETIME(6)
);

-- 3. Create Orders Table
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        table_number INT NOT NULL,
                        status VARCHAR(50) NOT NULL, -- Enum: PLACED, BILLED, etc.
                        total_amount BIGINT NOT NULL DEFAULT 0,
                        created_at DATETIME(6) NOT NULL
);

-- 4. Create Order Items Table
CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT,
                             menu_item_id BIGINT,
                             menu_item_name VARCHAR(255),
                             price_at_order_time BIGINT,
                             quantity INT,
                             subtotal BIGINT,

                             CONSTRAINT fk_order_items_orders
                                 FOREIGN KEY (order_id) REFERENCES orders(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_order_items_menu
                                 FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);