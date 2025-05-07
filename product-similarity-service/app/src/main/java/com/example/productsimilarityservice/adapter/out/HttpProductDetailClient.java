package com.example.productsimilarityservice.adapter.out;

import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.productsimilarityservice.domain.model.Product;

@Component
public class HttpProductDetailClient implements ProductDetailClient {

    private final WebClient webClient;

    public HttpProductDetailClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:3001").build();
    }

    @Override
    public Product fetchById(String productId) {
        return webClient
            .get()
            .uri("/product/{id}", productId)
            .retrieve()
            .bodyToMono(Product.class)
            .block();
    }
}
