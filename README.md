# Northstar Customer Center

Java 17、Jakarta EE 10 與 MongoDB 客戶營運網站，使用 WAR 格式部署於 JBoss EAP 8。

## 功能

- 營運管理員登入與 JWT 驗證
- MongoDB 健康狀態檢查
- 客戶清單查詢
- 建立客戶資料
- 建立客戶後透過 SMTP relay 寄送通知信
- SMTP 寄送失敗時在 Dashboard 顯示告警

## Dashboard 操作

1. 開啟 `http://localhost:8080/northstar-customer-center/`。
2. 按下「登入取得 JWT」。
3. 按下「驗證目前 JWT」，確認 JWT 簽章有效。
4. 新增客戶並輸入收件者 email。
5. 確認客戶資料已建立，並查看 SMTP 寄送結果通知。
6. 按下「清除 JWT」，確認客戶 API 因缺少 JWT 而無法載入資料。

## Build 與部署

```bash
mvn clean package

"$JBOSS_HOME/bin/jboss-cli.sh" --connect \
  --command="deploy target/northstar-customer-center.war --force"
```
