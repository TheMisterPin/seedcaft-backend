package com.michele.mocks.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Seedcraft API",
                description = "Realistic mock data API for ecommerce, logistics, inventory, and dashboard development",
                version = "v1"))
public class OpenApiConfig {
}
