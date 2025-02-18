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

# 動作確認方法
## アプリケーションの起動
```
./gradlew bootRun
```
上記のコマンドを実行してアプリケーションを起動した後、動作確認してください
またはIDEのGradleタブからbootRunタスクを実行しても構いません

## 動作確認手順
### 著者を登録する
```
curl -X POST -H "Content-Type: application/json" \
    -d '{"name": "Author Name", "birthDate": "1980-05-10"}' \
    http://localhost:8080/authors
```

### 著者の情報を更新する
```
curl -X PUT -H "Content-Type: application/json" \
    -d '{"name": "Updated Author", "birthDate": "1985-06-15"}' \
    http://localhost:8080/authors/1
```

### 書籍を登録する
```
curl -X POST -H "Content-Type: application/json" \
    -d '{"title": "The Great Book", "price": 3000, "status": "PUBLISHED", "authorIds": [1, 2]}' \
    http://localhost:8080/books
```

### 書籍の情報を更新する
```
curl -X PUT -H "Content-Type: application/json" \
    -d '{"title": "Updated Book Title", "price": 4000, "status": "PUBLISHED", "authorIds": [2]}' \
    http://localhost:8080/books/2
```

### 著者に紐づく書籍を取得する
```
curl http://localhost:8080/authors/1/books
```