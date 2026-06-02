package com.example.northstar.config;

import com.mongodb.ConnectionString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
class ApplicationConfigTest {

    @Test
    void mongodbUriContainsEncodedPasswordAndCanBeParsed() {
        ConnectionString connectionString = new ConnectionString(ApplicationConfig.MONGODB_URI);

        assertEquals("user1", connectionString.getCredential().getUserName());
        assertEquals("P@ssw0rd", new String(connectionString.getCredential().getPassword()));
        assertEquals("10.107.83.105:27017", connectionString.getHosts().get(0));
    }

}
