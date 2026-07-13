ALTER TABLE product
    ADD COLUMN category VARCHAR(32) NOT NULL DEFAULT 'SPORT_ACCESSORIES' AFTER name;

CREATE INDEX idx_product_category_status ON product(category, status);

UPDATE product SET category = 'SPORT_ACCESSORIES' WHERE category IS NULL OR category = '';

UPDATE product SET status = 'OFF_SALE'
WHERE name IN ('Java DDD 实战手册', 'Vue 3 商城前端模板', '支付可靠性设计笔记');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '疾风轻量缓震跑鞋', 'RUNNING', '轻量透气网面与回弹中底组合，适合日常慢跑、通勤和基础训练。', '/images/products/running-shoes.svg', 399.00, 88, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '疾风轻量缓震跑鞋');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '专业比赛七号篮球', 'BASKETBALL', '耐磨复合表皮，深沟设计带来稳定抓握，适合室内外训练与比赛。', '/images/products/basketball.svg', 159.00, 120, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '专业比赛七号篮球');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '跃界高帮篮球鞋', 'BASKETBALL', '高帮包裹与侧向支撑设计，兼顾启动、急停和落地缓震。', '/images/products/basketball-shoes.svg', 529.00, 56, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '跃界高帮篮球鞋');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '训练比赛五号足球', 'FOOTBALL', '标准五号热粘合足球，球形稳定，适合草地训练与业余比赛。', '/images/products/football.svg', 139.00, 94, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '训练比赛五号足球');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '猎速轻量足球鞋', 'FOOTBALL', '贴合鞋面配合多向碎钉大底，适合人工草场地快速移动。', '/images/products/football-shoes.svg', 459.00, 62, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '猎速轻量足球鞋');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '碳素进攻型羽毛球拍', 'BADMINTON', '轻量碳素材质与中杆弹性设计，适合进阶球友连续进攻。', '/images/products/badminton-racket.svg', 329.00, 71, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '碳素进攻型羽毛球拍');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '耐打训练羽毛球 12只装', 'BADMINTON', '复合软木球头与精选羽毛，飞行稳定，适合日常多球训练。', '/images/products/shuttlecock.svg', 69.00, 180, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '耐打训练羽毛球 12只装');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '可调节环保包胶哑铃', 'FITNESS', '防滑包胶握把与可调节配重组合，适合居家力量训练。', '/images/products/dumbbell.svg', 219.00, 49, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '可调节环保包胶哑铃');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '加厚防滑瑜伽垫', 'FITNESS', '高密度缓冲材质，双面防滑纹理，适合瑜伽、拉伸和核心训练。', '/images/products/yoga-mat.svg', 89.00, 130, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '加厚防滑瑜伽垫');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '多功能运动训练背包', 'OUTDOOR', '独立鞋仓、湿物分区与透气背负系统，适合球馆和短途户外。', '/images/products/sport-backpack.svg', 199.00, 76, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '多功能运动训练背包');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '户外保温运动水壶', 'OUTDOOR', '双层真空保温结构，防漏杯盖，适合徒步、骑行和日常训练。', '/images/products/sport-bottle.svg', 119.00, 143, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '户外保温运动水壶');

INSERT INTO product(name, category, description, cover_url, price, stock, status)
SELECT '高弹支撑运动护膝', 'PROTECTION', '分区编织与侧向支撑条设计，为跑步、深蹲和球类运动提供保护。', '/images/products/knee-support.svg', 79.00, 155, 'ON_SALE'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '高弹支撑运动护膝');