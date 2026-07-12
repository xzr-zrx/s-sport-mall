# 改造说明

旧版核心代码以单商品 `pay_order` 为中心，Controller 接收前端 `userId`，领域层直接调用支付宝 SDK，并使用 Redis 标记支付成功是否处理。2.0 版本采用新的 `mall_order`、`order_item`、`payment_order` 和 `payment_notify_log` 模型，旧数据表不再参与新接口。

本次交付未携带原仓库 `.git`、IDE 配置、历史日志、`target` 目录以及任何原支付/微信凭据。请勿继续使用原项目中曾出现过的密钥。
