# Vault Migration Demo

這是一個使用 Java 17、Jakarta EE 10、MongoDB 與 SMTP relay 的企業內部客戶管理範例，
使用 WAR 格式部署於 JBoss EAP 8。

目前程式刻意將部分設定 hardcode 在原始碼中，用於示範後續遷移至 Vault。以下只列出
實際有被程式執行使用的項目。

## Hardcoded 資訊盤點

設定集中在：

```text
src/main/java/com/example/vaultdemo/config/HardcodedSecrets.java
```

### MongoDB

| 常數 | 目前 hardcoded 值 | 實際用途 |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://user1:P%40ssw0rd@10.107.83.105:27017` | 連線 MongoDB。URI 內含帳號、URL encoded 密碼、IP 與 port。`P%40ssw0rd` 解碼後為 `P@ssw0rd`。 |
| `MONGODB_DATABASE` | `enterprise_demo` | 指定客戶資料存放的 MongoDB database。 |

後續導入 Vault 時，建議使用 Database Secrets Engine 動態產生短效帳密，或至少將
`username`、`password`、`host`、`port` 與 `database` 分開保存。

### JWT

| 常數 | 目前 hardcoded 值 | 實際用途 |
| --- | --- | --- |
| `JWT_SIGNING_KEY` | `demo-jwt-signing-key-change-before-production` | 使用 HMAC-SHA256 簽發及驗證 JWT。客戶查詢與新增 API 都需要有效 JWT。 |
| `DEMO_LOGIN_USERNAME` | `operator` | Dashboard 登入取得 JWT 的 demo 帳號。 |
| `DEMO_LOGIN_PASSWORD` | `demo-login-password` | Dashboard 登入取得 JWT 的 demo 密碼。 |

Vault 應保存 JWT signing key，而不是使用者登入後取得的 JWT token：

```text
secret/data/vault-migration-demo/jwt
  signingKey
```

### SMTP Relay

| 常數 | 目前 hardcoded 值 | 實際用途 |
| --- | --- | --- |
| `SMTP_HOST` | `10.107.85.47` | SMTP relay IP。 |
| `SMTP_PORT` | `25` | SMTP relay port。 |
| `SMTP_FROM_ADDRESS` | `vault-bob@palsys.com.tw` | 建立客戶後寄送通知信使用的寄件者。 |

SMTP relay 目前不需要帳號密碼。這些值不是傳統 credential，但仍會揭露可寄信的內部
基礎設施位置與寄件者設定，因此可作為敏感設定集中保存在 Vault：

```text
secret/data/vault-migration-demo/smtp
  host
  port
  fromAddress
```

除了保存設定，SMTP server 仍應限制來源 IP、寄件者、收件者範圍與寄送頻率。

## Dashboard 驗證方式

1. 開啟 `http://localhost:8080/vault-migration-demo/`。
2. 按下「登入取得 JWT」，使用 `operator` 與 `demo-login-password` 登入。
3. 按下「驗證目前 JWT」，確認 JWT 簽章有效。
4. 新增客戶並輸入收件者 email。
5. 確認客戶資料寫入 MongoDB，並查看 SMTP 寄送成功或失敗通知。
6. 按下「清除 JWT」，確認客戶 API 因缺少 JWT 而無法載入資料。

## Build 與部署

```bash
mvn clean package

"$JBOSS_HOME/bin/jboss-cli.sh" --connect \
  --command="deploy target/vault-migration-demo.war --force"
```
