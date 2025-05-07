package com.example.productsimilarityservice.adapter.out;

import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class HttpSimilarIdsClient implements SimilarIdsClient {

    private final WebClient webClient;

    public HttpSimilarIdsClient(WebClient.Builder builder) {
        // Base URL apuntando al mock en el puerto 3001
        this.webClient = builder
            .baseUrl("http://localhost:3001")
            .build();
    }

    @Override
    public List<String> fetchSimilarIds(String productId) {
        return webClient
            .get()
            .uri("/product/{id}/similarids", productId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
            .block();  
    }
}
