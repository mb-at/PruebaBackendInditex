package com.example.productsimilarityservice.adapter.out;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;

@Component
public class HttpSimilarIdsClient implements SimilarIdsClient {
    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(HttpSimilarIdsClient.class);

    public HttpSimilarIdsClient(WebClient.Builder builder,
                                @Value("${api.similarIds.base-url}") String baseUrl) {
        log.info("HttpSimilarIdsClient baseUrl = {}", baseUrl);
        this.webClient = builder
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public List<String> fetchSimilarIds(String productId) {
        return webClient.get()
            .uri("/product/{id}/similarids", productId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(List.of())     // por si timeout o 5xx/falla, devolvemos lista vacía
            .block();
    }
}