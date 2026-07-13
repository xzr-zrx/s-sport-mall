# Validation Record — SportHub Mall

## Completed

- Vue/TypeScript type check: `npm run type-check` — passed.
- Vue production build: `npm run build` — passed.
- MyBatis mapper XML files parsed as well-formed XML.
- Maven POM XML files parsed as well-formed XML.
- References to the old `createOrder(userId, cartItemIds)` signature were removed.
- Flyway migrations are ordered as V1, V2 and V3.
- Sensitive-value scan found no committed Alipay or WeChat private key material.

## Backend build note

The artifact environment did not contain Maven and could not resolve the package repository while installing it. Java source, interfaces, mapper statements and migration fields were checked statically, but the final backend Maven compilation must be run locally:

```bash
mvn -pl s-pay-mall-app -am clean test
mvn -pl s-pay-mall-app -am clean package -DskipTests
```

The easiest end-to-end verification is:

```bash
docker compose down -v
docker compose up --build
```

Removing the volume is recommended when testing the merged package from an older local database, so Flyway can create a clean schema including V3.
