-- Add MMA, boxing, kickboxing, BJJ and other fighting-sport products
-- This migration is idempotent: all INSERTs use WHERE NOT EXISTS to avoid
-- primary-key / unique-key conflicts on re-run.
-- The UPDATE is guarded by a WHERE clause that matches the old description.

-- Update knee support description to match fighting/grappling usage
UPDATE product
SET description = '分区编织与侧向支撑条设计，为深蹲、弓步和 grappling 提供保护。'
WHERE name = '高弹支撑运动护膝'
  AND description = '分区编织与侧向支撑条设计，为跑步、深蹲和球类运动提供保护。';

-- MMA products
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT 'MMA 训练手套 — 经典款', 'MMA', '高品质 MMA 训练手套，透气耐磨面料，适合日常打击与缠抱训练。', '/images/products/mma-gloves.svg', 259.00, 80, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'MMA 训练手套 — 经典款');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT 'MMA 紧身训练衣', 'MMA', '四向弹力面料，贴合身体曲线，排汗速干，适合综合格斗训练。', '/images/products/mma-rashguard.svg', 219.00, 70, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'MMA 紧身训练衣');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '格斗训练短裤', 'MMA', '轻量透气面料，侧开叉设计确保踢腿灵活性，内衬网眼短裤。', '/images/products/fight-shorts.svg', 239.00, 55, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '格斗训练短裤');

-- Boxing products
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '专业拳击手套 12oz', 'BOXING', '优质 PU 面料加厚填充，12oz 标准训练重量，适合沙袋与对练。', '/images/products/boxing-gloves.svg', 329.00, 65, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '专业拳击手套 12oz');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '弹性绑手带 4.5m', 'BOXING', '高弹力棉质绑手带，提供手腕与拳峰支撑，减少训练时手部受伤风险。', '/images/products/hand-wraps.svg', 49.00, 250, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '弹性绑手带 4.5m');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '拳击脚靶 — 一对装', 'BOXING', '高密度泡棉填充，弧形贴合手臂，适合搭档配合打击训练。', '/images/products/punch-mitts.svg', 189.00, 40, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '拳击脚靶 — 一对装');

-- Kickboxing products
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '泰拳护胫 — 中长款', 'KICKBOXING', '高密度护胫垫片，贴合腿部曲线，有效吸收踢击冲击力。', '/images/products/shin-guards.svg', 269.00, 48, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '泰拳护胫 — 中长款');

-- BJJ / Grappling products
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '巴西柔术道服 — 竞赛版', 'BJJ', '合身剪裁竞赛级道服，加固缝合设计，经得起高强度训练。', '/images/products/bjj-gi.svg', 599.00, 35, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '巴西柔术道服 — 竞赛版');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '无道服训练短袖短裤套装', 'BJJ', '速干弹性面料，贴合不束缚，适合 grappling 和柔术训练。', '/images/products/no-gi-set.svg', 349.00, 42, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '无道服训练短袖短裤套装');

-- Protection gear
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '运动护齿 — 可塑成型', 'PROTECTION', '医用级硅胶材质，热水塑形贴合齿弓，提供有效咬合保护。', '/images/products/mouthguard.svg', 79.00, 200, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '运动护齿 — 可塑成型');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '头部护具 — 全包裹式', 'PROTECTION', '加厚护垫覆盖额头、脸颊与后脑，视野开阔，适合 sparring 训练。', '/images/products/head-guard.svg', 389.00, 30, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '头部护具 — 全包裹式');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '护裆 — 擂台款', 'PROTECTION', '硬质护壳与弹性腰带，贴身稳固，有效吸收正面冲击。', '/images/products/groin-guard.svg', 159.00, 90, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '护裆 — 擂台款');

-- Fitness / conditioning products for fighters
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '反应球训练套装', 'FITNESS', '弹性绳连接头带与反应球，锻炼手眼协调与反应速度。', '/images/products/reaction-ball.svg', 59.00, 110, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '反应球训练套装');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '专业竞速跳绳', 'FITNESS', '防缠绕钢丝绳芯，可调节长度，高速轴承手柄，适合格斗体能训练。', '/images/products/jump-rope.svg', 69.00, 130, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '专业竞速跳绳');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '竞技壶铃 16kg', 'FITNESS', '铸铁一体成型，宽大把手，适合 swing、clean 等爆发力训练动作。', '/images/products/kettlebell.svg', 299.00, 25, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '竞技壶铃 16kg');

-- Recovery products
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '高密度泡沫轴', 'RECOVERY', 'EVA 高密度发泡材质，表面凸点按摩，有效缓解训练后肌肉紧张。', '/images/products/foam-roller.svg', 129.00, 60, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '高密度泡沫轴');

-- Merchandise
INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '格斗训练背包', 'MERCH', '大容量主仓与独立鞋仓设计，耐磨面料，可携带全套训练装备。', '/images/products/fight-bag.svg', 359.00, 38, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '格斗训练背包');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '格斗主题 T 恤 — 经典 Logo', 'MERCH', '纯棉圆领短袖 T 恤，正面印花格斗主题图案，日常出街训练皆宜。', '/images/products/fight-tshirt.svg', 129.00, 85, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '格斗主题 T 恤 — 经典 Logo');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '格斗主题棒球帽', 'MERCH', '纯色六片拼接棒球帽，刺绣 Logo，可调节金属扣带。', '/images/products/fight-cap.svg', 99.00, 100, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '格斗主题棒球帽');