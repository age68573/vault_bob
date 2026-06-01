package com.example.vaultdemo.rest;

import com.example.vaultdemo.config.HardcodedSecrets;
import com.example.vaultdemo.security.AuthSupport;
import com.example.vaultdemo.security.JwtService;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(JsonObject request) {
        String username = request.getString("username", "");
        String password = request.getString("password", "");
        if (!HardcodedSecrets.DEMO_LOGIN_USERNAME.equals(username)
                || !HardcodedSecrets.DEMO_LOGIN_PASSWORD.equals(password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "帳號或密碼錯誤"))
                    .build();
        }

        return Response.ok(Map.of(
                "accessToken", JwtService.issue(username),
                "tokenType", "Bearer",
                "expiresIn", JwtService.tokenValidSeconds()
        )).build();
    }

    @GET
    @Path("/profile")
    public Response profile(@jakarta.ws.rs.core.Context HttpHeaders headers) {
        JwtService.TokenValidation validation = AuthSupport.validate(headers);
        if (!validation.valid()) return AuthSupport.unauthorized(validation);
        return Response.ok(Map.of(
                "username", validation.subject(),
                "role", "operator",
                "expiresAt", validation.expiresAt(),
                "message", validation.message()
        )).build();
    }
}
