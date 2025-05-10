package com.example.productsimilarityservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI productSimilarityAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Similarity Service API")
                .version("v1")
                .description("Servicio para obtener productos similares"));
    }
}