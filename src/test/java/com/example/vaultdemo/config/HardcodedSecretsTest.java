package com.example.vaultdemo.config;

import com.mongodb.ConnectionString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HardcodedSecretsTest {

    @Test
    void mongodbUriContainsEncodedPasswordAndCanBeParsed() {
        ConnectionString connectionString = new ConnectionString(HardcodedSecrets.MONGODB_URI);

        assertEquals("user1", connectionString.getCredential().getUserName());
        assertEquals("P@ssw0rd", new String(connectionString.getCredential().getPassword()));
        assertEquals("10.107.83.105:27017", connectionString.getHosts().get(0));
    }

    @Test
    void migrationInventoryDescribesCandidatesWithoutReturningSecretValues() {
        assertFalse(HardcodedSecrets.candidates().isEmpty());
        assertTrue(HardcodedSecrets.candidates().stream()
                .allMatch(candidate -> candidate.getSuggestedVaultPath().startsWith("secret/data/")));
        assertTrue(HardcodedSecrets.candidates().stream()
                .noneMatch(candidate -> candidate.getScenario().contains("P%40ssw0rd")));
    }
}
