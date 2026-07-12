ALTER TABLE mall_order
    ADD COLUMN source VARCHAR(20) NOT NULL DEFAULT 'CART' AFTER user_id,
    ADD COLUMN recipient_name VARCHAR(64) NOT NULL DEFAULT '' AFTER source,
    ADD COLUMN recipient_phone VARCHAR(24) NOT NULL DEFAULT '' AFTER recipient_name,
    ADD COLUMN shipping_address VARCHAR(255) NOT NULL DEFAULT '' AFTER recipient_phone;

CREATE INDEX idx_order_user_status_time ON mall_order(user_id, status, create_time);
