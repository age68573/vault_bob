package com.example.vaultdemo.rest;

import com.example.vaultdemo.config.HardcodedSecrets;
import com.example.vaultdemo.config.MongoClientProvider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    @GET
    public List<Map<String, Object>> list(@QueryParam("limit") Integer requestedLimit) {
        int limit = requestedLimit == null ? DEFAULT_LIMIT : Math.max(1, Math.min(requestedLimit, MAX_LIMIT));
        List<Map<String, Object>> customers = new ArrayList<>();

        collection().find()
                .sort(Sorts.descending("createdAt"))
                .limit(limit)
                .forEach(document -> customers.add(toResponse(document)));

        return customers;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(JsonObject request) {
        String name = trimmed(request, "name");
        String email = trimmed(request, "email");

        if (name.isEmpty() || email.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "name and email are required"))
                    .build();
        }

        Document customer = new Document("_id", new ObjectId())
                .append("name", name)
                .append("email", email)
                .append("createdAt", Instant.now().toString());
        collection().insertOne(customer);

        return Response.status(Response.Status.CREATED).entity(toResponse(customer)).build();
    }

    private MongoCollection<Document> collection() {
        return MongoClientProvider.get().getDatabase(HardcodedSecrets.MONGODB_DATABASE)
                .getCollection("customers");
    }

    private static String trimmed(JsonObject request, String name) {
        return request.getString(name, "").trim();
    }

    private static Map<String, Object> toResponse(Document document) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", document.getObjectId("_id").toHexString());
        response.put("name", document.getString("name"));
        response.put("email", document.getString("email"));
        response.put("createdAt", document.getString("createdAt"));
        return response;
    }
}
