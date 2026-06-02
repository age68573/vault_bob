package com.example.northstar.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public final class MongoClientProvider {

    private static volatile MongoClient mongoClient;

    private MongoClientProvider() {
    }

    public static MongoClient get() {
        MongoClient result = mongoClient;
        if (result == null) {
            synchronized (MongoClientProvider.class) {
                result = mongoClient;
                if (result == null) {
                    result = MongoClients.create(ApplicationConfig.mongodbUri());
                    mongoClient = result;
                }
            }
        }
        return result;
    }

    public static void close() {
        MongoClient result = mongoClient;
        if (result != null) {
            result.close();
            mongoClient = null;
        }
    }
}
