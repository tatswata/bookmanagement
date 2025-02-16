# book management
book management system for coding test

# design doc
[SUDOモデリング](https://miro.com/app/board/uXjVIeBhZUo=/?share_link_id=17607207850)

# SETUP
## 前提
以下のセットアップが完了していること
- Docker
- JDK(java21)
- kotlin

## DBセットアップ
```
docker compose -f compose.yaml up -d
./gradlew flywayMigrate
```

## jOOQでコード生成
```
./gradlew jooqCodegen
```