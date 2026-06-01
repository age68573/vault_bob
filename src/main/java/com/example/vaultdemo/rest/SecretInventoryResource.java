package com.example.vaultdemo.rest;

import com.example.vaultdemo.config.HardcodedSecrets;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/vault-migration-candidates")
@Produces(MediaType.APPLICATION_JSON)
public class SecretInventoryResource {

    @GET
    public List<HardcodedSecrets.SecretCandidate> list() {
        return HardcodedSecrets.candidates();
    }
}
