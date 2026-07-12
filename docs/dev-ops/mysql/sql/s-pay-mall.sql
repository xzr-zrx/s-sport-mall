CREATE TABLE IF NOT EXISTS user_account (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(100) NULL,
  nickname VARCHAR(64) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  wechat_open_id VARCHAR(64) NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id), UNIQUE KEY uk_username(username), UNIQUE KEY uk_wechat_open_id(wechat_open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS product (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(1000) NULL,
  cover_url VARCHAR(500) NULL,
  price DECIMAL(10,2) NOT NULL,
  stock INT UNSIGNED NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'ON_SALE',
  version INT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), KEY idx_product_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cart_item (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  quantity INT UNSIGNED NOT NULL,
  selected TINYINT(1) NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_cart_user_product(user_id,product_id), KEY idx_cart_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_order (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  order_no VARCHAR(32) NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  pay_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(32) NOT NULL,
  expire_at DATETIME NOT NULL,
  pay_time DATETIME NULL,
  close_time DATETIME NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_order_no(order_no), KEY idx_order_user(user_id), KEY idx_order_status_expire(status,expire_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_item (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  order_no VARCHAR(32) NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  product_name VARCHAR(100) NOT NULL,
  product_cover VARCHAR(500) NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  quantity INT UNSIGNED NOT NULL,
  item_amount DECIMAL(10,2) NOT NULL,
  PRIMARY KEY(id), KEY idx_order_item_order(order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payment_order (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  payment_no VARCHAR(40) NOT NULL,
  order_no VARCHAR(32) NOT NULL,
  channel VARCHAR(20) NOT NULL,
  pay_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  third_party_trade_no VARCHAR(64) NULL,
  pay_time DATETIME NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), UNIQUE KEY uk_payment_no(payment_no), UNIQUE KEY uk_payment_order(order_no), UNIQUE KEY uk_third_party_trade_no(third_party_trade_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payment_notify_log (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  order_no VARCHAR(32) NULL,
  third_party_trade_no VARCHAR(64) NULL,
  request_hash VARCHAR(64) NULL,
  result VARCHAR(255) NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id), KEY idx_notify_order(order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS outbox_event (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  aggregate_id VARCHAR(64) NOT NULL,
  event_type VARCHAR(64) NOT NULL,
  payload TEXT NOT NULL,
  status VARCHAR(20) NOT NULL,
  retry_count INT NOT NULL DEFAULT 0,
  published_time DATETIME NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id), KEY idx_outbox_status(status,id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO product(name,description,cover_url,price,stock,status)
SELECT 'Java DDD 实战手册','用于演示下单、支付回调、主动查单和超时关单的数字商品','https://picsum.photos/seed/java-ddd/600/400',19.90,999,'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product);
INSERT INTO product(name,description,cover_url,price,stock,status)
SELECT 'Vue 3 商城前端模板','Vue 3 + TypeScript + Vite 的商城前端示例','https://picsum.photos/seed/vue3/600/400',9.90,999,'ON_SALE'
WHERE (SELECT COUNT(*) FROM product)=1;
INSERT INTO product(name,description,cover_url,price,stock,status)
SELECT '支付可靠性设计笔记','包含回调验签、幂等、主动查单、超时关单和 Outbox','https://picsum.photos/seed/payment/600/400',29.90,999,'ON_SALE'
WHERE (SELECT COUNT(*) FROM product)=2;
