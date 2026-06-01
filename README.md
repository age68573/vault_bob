# Vault Migration Demo

這是一個使用 Java 17、Jakarta EE 10 與 MongoDB 的企業內部客戶管理範例，使用 WAR
格式部署於 JBoss EAP 8。

此專案刻意將部分密鑰與環境設定 hardcode 在程式碼中，用於示範未來導入 Vault
時的盤點、分類與遷移流程。這種設定方式不適合正式環境。

## 目前狀態

- WAR 可部署至 JBoss EAP 8。
- 應用程式已驗證可以正常連線至 MongoDB。
- Dashboard 可顯示 MongoDB 健康狀態、登入取得 JWT、驗證 JWT、查詢客戶清單，並建立客戶資料。
- 建立客戶後，應用程式會透過 SMTP relay 寄送通知信。寄送失敗時，Dashboard 會顯示告警。
- Vault 遷移清單頁面只顯示項目用途與建議 Vault path，不會透過 API 回傳密鑰值。

## Hardcoded 資訊盤點

所有刻意 hardcode 的資訊集中在：

```text
src/main/java/com/example/vaultdemo/config/HardcodedSecrets.java
```

### 應優先移至 Vault 的機敏資訊

| 常數 | 目前 hardcoded 值 | 使用情境 | 建議 Vault path |
| --- | --- | --- | --- |
| `MONGODB_URI` | `mongodb://user1:P%40ssw0rd@10.107.83.105:27017` | MongoDB 連線 URI，包含帳號、URL encoded 密碼與主機位置。`P%40ssw0rd` 解碼後為 `P@ssw0rd`。 | `secret/data/vault-migration-demo/mongodb` |
| `JWT_SIGNING_KEY` | `demo-jwt-signing-key-change-before-production` | 應用程式簽發與驗證 JWT 的簽章密鑰。 | `secret/data/vault-migration-demo/jwt` |
| `DEMO_LOGIN_USERNAME` | `operator` | JWT demo 登入帳號。 | `secret/data/vault-migration-demo/login` |
| `DEMO_LOGIN_PASSWORD` | `demo-login-password` | JWT demo 登入密碼。 | `secret/data/vault-migration-demo/login` |
| `SMTP_HOST` | `10.107.85.47` | 無帳密 SMTP relay IP。此資訊不是密碼，但會揭露可寄信的內部基礎設施位置。 | `secret/data/vault-migration-demo/smtp` |
| `SMTP_PORT` | `25` | 無帳密 SMTP relay port。 | `secret/data/vault-migration-demo/smtp` |
| `SMTP_FROM_ADDRESS` | `vault-bob@palsys.com.tw` | SMTP relay 使用的寄件者設定。 | `secret/data/vault-migration-demo/smtp` |
| `PARTNER_API_KEY` | `partner_demo_7d39d6b662b94b4b` | 呼叫外部合作夥伴 API 使用的 API key。 | `secret/data/vault-migration-demo/partner-api` |
| `KEYSTORE_PASSWORD` | `demo-keystore-password` | 讀取 TLS client keystore 使用的密碼。 | `secret/data/vault-migration-demo/tls` |
| `INTERNAL_BASIC_AUTH_USERNAME` | `settlement-service` | 呼叫 legacy 內部服務使用的 Basic Auth 帳號。 | `secret/data/vault-migration-demo/internal-service` |
| `INTERNAL_BASIC_AUTH_PASSWORD` | `demo-basic-auth-password` | 呼叫 legacy 內部服務使用的 Basic Auth 密碼。 | `secret/data/vault-migration-demo/internal-service` |
| `WEBHOOK_HMAC_SECRET` | `demo-webhook-hmac-secret` | 驗證 webhook 簽章使用的共享密鑰。 | `secret/data/vault-migration-demo/webhook` |

### 建議外部化，但不一定需要放入 Vault 的資訊

| 常數 | 目前 hardcoded 值 | 使用情境 | 建議處理方式 |
| --- | --- | --- | --- |
| `MONGODB_DATABASE` | `enterprise_demo` | MongoDB database 名稱。 | 移至環境變數、MicroProfile Config 或 JBoss system property。 |
| `KEYSTORE_PATH` | `/opt/eap/standalone/configuration/demo-client.p12` | TLS client keystore 檔案位置。 | 路徑本身通常可放在環境設定；keystore 檔案與密碼應分別妥善保管。 |

### 分類說明

- `MONGODB_URI` 同時包含帳號、密碼與基礎設施位置，正式環境至少應將認證資訊移至
  Vault。更完整的做法是分別保存 `username`、`password`、`host` 與 `database`。
- SMTP relay 不使用帳號密碼，但 `SMTP_HOST`、`SMTP_PORT` 與寄件者設定仍屬敏感資訊。
  除了保存在 Vault，也應在 SMTP server 端限制來源 IP、寄件者與寄送頻率。
- 帳號名稱未必等同密碼，但仍可能揭露內部服務用途，因此本範例將帳號與密碼一起納管。
- database 名稱與檔案路徑通常不是密鑰，但 hardcode 會降低環境切換能力，仍建議外部化。

## Vault 遷移優先順序

1. 優先遷移 `MONGODB_URI`，因為目前應用程式已實際使用此設定連線 MongoDB。
2. 遷移 JWT signing key，避免簽章密鑰長期存在於原始碼。
3. 遷移 SMTP relay 敏感設定，降低內部寄信基礎設施資訊散落的風險。
4. 將非密鑰環境設定改由環境變數、MicroProfile Config 或 JBoss system property 提供。
5. 完成遷移後，從 Git 歷史紀錄移除曾經提交的密鑰，並進行密鑰輪替。

## Build 與部署

環境需求：

- JDK 17
- Maven 3.8+
- JBoss EAP 8
- JBoss EAP 主機可以連線至 `10.107.83.105:27017`

Build：

```bash
mvn clean package
```

Deploy：

```bash
"$JBOSS_HOME/bin/jboss-cli.sh" --connect \
  --command="deploy target/vault-migration-demo.war --force"
```

## API

```text
GET  http://localhost:8080/vault-migration-demo/api/health
POST http://localhost:8080/vault-migration-demo/api/auth/login
GET  http://localhost:8080/vault-migration-demo/api/auth/profile
GET  http://localhost:8080/vault-migration-demo/api/customers?limit=20
POST http://localhost:8080/vault-migration-demo/api/customers
GET  http://localhost:8080/vault-migration-demo/api/vault-migration-candidates
```

建立客戶資料：

```bash
curl -X POST http://localhost:8080/vault-migration-demo/api/customers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ada Lovelace","email":"ada@example.com"}'
```

客戶 API 需要先透過 Dashboard 登入取得 JWT。Demo 登入帳密為：

```text
username: operator
password: demo-login-password
```

最容易理解的展示方式是在 Dashboard 操作：

1. 按下「登入取得 JWT」，使用 demo 帳密登入。
2. 按下「驗證目前 JWT」，確認簽章有效與 token 到期時間。
3. 建立客戶資料，確認 MongoDB 寫入成功，並觀察 SMTP 寄信通知。
4. 按下「清除 JWT」，確認客戶資料無法載入，證明 API 確實受到 JWT 保護。

## JBoss EAP 注意事項

此 WAR 不使用 CDI，因此透過 `WEB-INF/jboss-deployment-structure.xml` 停用 Weld subsystem，
並使用應用程式內的 lazy singleton 管理 MongoClient。這可以降低部署階段的 Metaspace
使用量。

若 JBoss EAP 仍出現 `java.lang.OutOfMemoryError: Metaspace`，代表目前 JVM 的
Metaspace 上限不足。可以在啟動 JBoss 前暫時增加 JVM 參數：

```bash
export JAVA_OPTS="$JAVA_OPTS -XX:MaxMetaspaceSize=512m"
"$JBOSS_HOME/bin/standalone.sh" -b 0.0.0.0
```
