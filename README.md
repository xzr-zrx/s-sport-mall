# S-Pay Mall DDD 2.0

一个可独立部署的全栈商城支付项目：Vue 3 前端 + Spring Boot Java 后端，保留 DDD 分层思路并补齐账号登录、商品、购物车、多商品订单和可靠支付链路。

## 已完成的改造

- 用户名密码注册登录，BCrypt 存储密码，JWT 鉴权；微信扫码成功后也签发 JWT。
- 下单接口不再接收 `userId`，用户身份只从 JWT 读取。
- 商品从 MySQL 查询，支持库存条件扣减、商品快照和管理端维护。
- 支持购物车、多商品结算、立即购买、确认订单、订单列表、订单详情和取消订单。
- 订单与支付单分表，支付回调记录独立落库。
- 支付宝 SDK 从领域服务移到基础设施支付网关端口。
- 回调除验签外，还校验 `app_id`、交易状态和本地订单金额。
- 主动查单明确判断 `TRADE_SUCCESS` / `TRADE_FINISHED`，不再仅凭接口调用成功更新订单。
- 超时关单先查询支付状态，再调用支付宝关单，最后条件关闭本地订单并恢复库存。
- 支付成功使用数据库条件更新保证幂等，并写入 Outbox 事件，不再依赖 Redis “已处理”标记。
- 密钥全部改为环境变量，历史日志、构建产物和原仓库 `.git` 已从交付包移除。
- 新增 Docker、Docker Compose、Render Blueprint、健康检查和 GitHub Actions。
- 默认关闭支付宝，提供本地模拟支付页面；配置沙箱密钥后可切换为真实支付宝沙箱。


## 体育商城第一、二轮升级

第一轮：

- 品牌升级为 **S-SPORT 综合体育用品商城**。
- 新增独立首页、运动分类、热门商品、新品推荐和服务保障模块。
- 商品列表支持关键词、分类、价格排序和分页。
- 商品详情增加库存状态、数量限制、图片兜底、同类推荐和响应式布局。
- Flyway `V2__sport_product_catalog.sql` 新增体育分类和 12 条演示商品，旧数字商品会下架但不会删除订单快照。

第二轮：

- 商品卡片和商品详情均支持“立即购买”，可跳过购物车进入确认订单。
- 购物车增加全选、库存校验、数量更新、删除确认、选中件数和实时金额汇总。
- 新增确认订单页面，填写收货人、联系电话和详细地址；后端重新查询价格、库存并创建订单。
- 购物车结算与立即购买复用统一安全下单接口，前端不传最终价格和用户编号。
- 新增订单支付页，展示支付倒计时、订单明细、支付方式，并轮询后端订单状态。
- 模拟支付成功后向商城窗口发送通知，商城仍以后端订单状态为最终结果。
- 我的订单增加状态筛选、分页、取消订单和立即支付；订单详情展示收货信息与完整时间信息。
- Flyway `V3__order_checkout_fields.sql` 为订单增加来源与收货信息字段。

## 技术栈

后端：Java 17、Spring Boot 2.7、Spring Security、MyBatis、MySQL、Redis/Redisson、JWT、Flyway、支付宝 SDK。

前端：Vue 3、TypeScript、Vite、Vue Router、Pinia、Axios、Element Plus。

## 本地启动

最简单的方式：

```bash
docker compose up --build
```

打开：

- 前端：http://localhost:5173
- 后端健康检查：http://localhost:8091/actuator/health

首次使用在注册页面创建账号。默认 `ALIPAY_ENABLED=false`，支付时会打开带 HMAC 签名校验的本地模拟收银台。

## 手动启动

先启动 MySQL 和 Redis，再配置环境变量：

```bash
cp .env.example .env
mvn -pl s-pay-mall-app -am clean package
java -jar s-pay-mall-app/target/s-pay-mall-app.jar
```

前端：

```bash
cd s-pay-mall-web
npm install
npm run dev
```

## 支付宝沙箱

设置以下环境变量：

```text
ALIPAY_ENABLED=true
ALIPAY_APP_ID=...
ALIPAY_MERCHANT_PRIVATE_KEY=...
ALIPAY_PUBLIC_KEY=...
ALIPAY_NOTIFY_URL=https://你的后端域名/api/v1/payments/alipay/notify
ALIPAY_RETURN_URL=https://你的前端域名/payment-result
```

私钥、公钥不要写入 Git 仓库。旧项目中出现过的密钥应在平台侧作废并重新生成。

## Render 部署

仓库根目录已经提供 `render.yaml`。部署前准备：

1. 一个可外网访问的 MySQL 数据库。
2. 一个 Redis/Valkey 实例。
3. 在 Render 后端服务中填写 `DATABASE_URL`、数据库账号密码和 `REDIS_URL`。
4. 将前端 `VITE_API_BASE_URL` 设置为 Render 后端地址，并将后端 `BACKEND_PUBLIC_URL` 设置为同一后端公网地址。
5. 将后端 `CORS_ALLOWED_ORIGINS` 设置为 Render 前端地址。
6. 正式启用沙箱支付时填写支付宝环境变量，并将回调地址改为 HTTPS 公网地址。

Render 免费 Web Service 可能休眠。需要稳定执行补偿任务时，可关闭应用内任务并用定时服务携带 `X-Job-Token` 调用：

```text
POST /internal/jobs/payment-reconcile
POST /internal/jobs/timeout-close
POST /internal/jobs/outbox-publish
```

## 管理员权限

普通注册用户角色为 `USER`。需要使用商品管理接口时，可在数据库执行：

```sql
UPDATE user_account SET role='ADMIN' WHERE username='你的用户名';
```

重新登录后 JWT 中会包含管理员角色。

## 主要接口

商品列表接口现在支持：

```text
GET /api/v1/products?keyword=篮球&category=BASKETBALL&sort=PRICE_ASC&page=1&pageSize=12
```

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/products`
- `GET /api/v1/cart`
- `POST /api/v1/cart/items`
- `POST /api/v1/orders`
- `GET /api/v1/orders`
- `POST /api/v1/payments/alipay/{orderNo}`
- `POST /api/v1/payments/alipay/notify`

创建订单支持两种来源：

```json
{
  "source": "BUY_NOW",
  "items": [{ "productId": 1, "quantity": 2 }],
  "recipientName": "张三",
  "recipientPhone": "13800000000",
  "shippingAddress": "上海市浦东新区示例路 88 号"
}
```

购物车结算时使用 `source=CART` 并提交 `cartItemIds`。用户编号、商品价格和订单总额都由后端确定。

## 构建验证

合并版前端已通过 `npm run type-check` 和 `npm run build`。本次执行环境无法联网安装 Maven，因此 Java 侧完成了接口引用、MyBatis XML、Flyway 脚本和配置静态检查；请在本机执行 `docker compose up --build` 或 `mvn -pl s-pay-mall-app -am clean package` 完成后端实际构建。详细记录见 `docs/VALIDATION.md`。
