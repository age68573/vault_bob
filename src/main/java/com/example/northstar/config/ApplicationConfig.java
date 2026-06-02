package com.example.northstar.config;

public final class ApplicationConfig {

    public static final String MONGODB_URI =
            "mongodb://user1:P%40ssw0rd@10.107.83.105:27017";
    public static final String MONGODB_DATABASE = "enterprise_demo";

    public static final String JWT_SIGNING_KEY =
            "demo-jwt-signing-key-change-before-production";
    public static final String DEMO_LOGIN_USERNAME = "operator";
    public static final String DEMO_LOGIN_PASSWORD = "demo-login-password";
    public static final String SMTP_HOST = "10.107.85.47";
    public static final int SMTP_PORT = 25;
    public static final String SMTP_FROM_ADDRESS = "vault-bob@palsys.com.tw";

    private ApplicationConfig() {
    }
}
