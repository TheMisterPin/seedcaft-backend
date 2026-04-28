package com.michele.mocks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public HealthResponse healthV1() {
        return new HealthResponse("ok", "seedcraft-api", "v1");
    }

    @GetMapping("/api/health")
    public HealthResponse healthLegacy() {
        return healthV1();
    }

    public record HealthResponse(String status, String service, String version) {
    }
}
