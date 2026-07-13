# 斗牛士综合格斗商城

一个综合格斗主题的前后端分离商城项目，专注 MMA 综合格斗、拳击与踢拳、柔术与摔跤、防护装备、力量与体能训练器材及格斗主题周边商品。提供用户注册登录、商品浏览、购物车、订单结算、模拟支付和订单管理等完整购物流程。

后端采用 Spring Boot，前端采用 Vue 3；MySQL 用于保存用户、商品、订单和支付数据，Redis 用于缓存及临时状态。项目默认使用模拟支付，不需要配置真实支付宝密钥。

主要功能

- 用户注册、登录与 JWT 鉴权
- 格斗商品浏览、搜索和详情展示
- 购物车与订单结算
- 订单创建、查询和取消
- 模拟支付与订单状态更新
- 商品库存扣减与恢复

技术栈

后端： Java 17、Spring Boot、Spring Security、MyBatis、MySQL、Redis、Flyway、JWT  
前端： Vue 3、TypeScript、Vite、Pinia、Axios、Element Plus

在线访问

- 前端网站：`https://s-pay-mall-web-3bdh.onrender.com`（斗牛士综合格斗商城）
- 后端健康检查：`https://s-pay-mall-api-3bdh.onrender.com/actuator/health`

Render 免费版访问说明

后端使用 Render 免费 Web Service。连续约 15 分钟没有请求时，后端会自动休眠；再次启动通常需要约 1 分钟。前端是静态站点，可以正常打开，但后端休眠期间，登录、商品、订单等接口可能暂时提示"网络连接异常"。

如果网站暂时无法正常使用，请按以下步骤操作：

1. 先打开后端健康检查地址：  
   `https://s-pay-mall-api-3bdh.onrender.com/actuator/health`
2. 等待页面返回：
   ```json
   {"status":"UP"}
   ```
3. 再刷新前端网站并继续使用。

本地启动

Docker Compose 启动

在项目根目录执行：

```bash
docker compose up --build
```

启动后访问：

- 前端：http://localhost:5173
- 后端健康检查：http://localhost:8091/actuator/health

手动启动

先启动 MySQL 和 Redis，并根据 `.env.example` 配置环境变量。

启动后端：

```bash
mvn -pl s-pay-mall-app -am clean package
java -jar s-pay-mall-app/target/s-pay-mall-app.jar
```

启动前端：

```bash
cd s-pay-mall-web
npm install
npm run dev
```