package com.example.vaultdemo.config;

import java.util.List;

/**
 * Intentionally insecure configuration for a Vault migration demonstration.
 *
 * Never use this pattern in a production application. Each value is kept here
 * only so it can be discovered, classified, and replaced by Vault integration.
 */
public final class HardcodedSecrets {

    public static final String MONGODB_URI =
            "mongodb://user1:P%40ssw0rd@10.107.83.105:27017";
    public static final String MONGODB_DATABASE = "enterprise_demo";

    public static final String JWT_SIGNING_KEY =
            "demo-jwt-signing-key-change-before-production";
    public static final String DEMO_LOGIN_USERNAME = "operator";
    public static final String DEMO_LOGIN_PASSWORD = "demo-login-password";
    public static final String SMTP_HOST = "10.107.85.47";
    public static final int SMTP_PORT = 25;
    public static final String SMTP_FROM_ADDRESS = "vault-bob.palsys.com.tw";
    public static final String PARTNER_API_KEY =
            "partner_demo_7d39d6b662b94b4b";
    public static final String KEYSTORE_PATH = "/opt/eap/standalone/configuration/demo-client.p12";
    public static final String KEYSTORE_PASSWORD = "demo-keystore-password";
    public static final String INTERNAL_BASIC_AUTH_USERNAME = "settlement-service";
    public static final String INTERNAL_BASIC_AUTH_PASSWORD = "demo-basic-auth-password";
    public static final String WEBHOOK_HMAC_SECRET = "demo-webhook-hmac-secret";

    private static final List<SecretCandidate> CANDIDATES = List.of(
            new SecretCandidate("mongodb-uri", "MongoDB connection URI with username and password",
                    "secret/data/vault-migration-demo/mongodb"),
            new SecretCandidate("jwt-signing-key", "Application JWT signing key",
                    "secret/data/vault-migration-demo/jwt"),
            new SecretCandidate("smtp-relay-settings", "SMTP relay host, port, and sender identity",
                    "secret/data/vault-migration-demo/smtp"),
            new SecretCandidate("partner-api-key", "External partner API key",
                    "secret/data/vault-migration-demo/partner-api"),
            new SecretCandidate("smtp-credentials", "SMTP username and password for notifications",
                    "secret/data/vault-migration-demo/smtp"),
            new SecretCandidate("keystore-password", "TLS client keystore password",
                    "secret/data/vault-migration-demo/tls"),
            new SecretCandidate("internal-basic-auth", "Legacy internal service credentials",
                    "secret/data/vault-migration-demo/internal-service"),
            new SecretCandidate("webhook-hmac-secret", "Shared secret used to verify webhook signatures",
                    "secret/data/vault-migration-demo/webhook")
    );

    private HardcodedSecrets() {
    }

    public static List<SecretCandidate> candidates() {
        return CANDIDATES;
    }

    public static final class SecretCandidate {
        private final String id;
        private final String scenario;
        private final String suggestedVaultPath;

        public SecretCandidate(String id, String scenario, String suggestedVaultPath) {
            this.id = id;
            this.scenario = scenario;
            this.suggestedVaultPath = suggestedVaultPath;
        }

        public String getId() {
            return id;
        }

        public String getScenario() {
            return scenario;
        }

        public String getSuggestedVaultPath() {
            return suggestedVaultPath;
        }
    }
}
