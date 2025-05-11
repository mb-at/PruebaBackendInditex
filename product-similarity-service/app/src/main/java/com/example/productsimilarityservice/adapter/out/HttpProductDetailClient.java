package com.example.productsimilarityservice.adapter.out;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;

import reactor.core.publisher.Mono;

@Component
public class HttpProductDetailClient implements ProductDetailClient {

    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(HttpProductDetailClient.class);

    public HttpProductDetailClient(WebClient.Builder builder,
                                   @Value("${api.productDetail.base-url}") String baseUrl) {
        log.info("HttpProductDetailClient baseUrl = {}", baseUrl);
        this.webClient = builder
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Product fetchById(String productId) {
        Mono<Product> mono = webClient.get()
            .uri("/product/{id}", productId)
            .retrieve()
            .bodyToMono(Product.class);
        return mono
            .timeout(Duration.ofSeconds(3))
            .block();
    }
}
