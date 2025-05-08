package com.example.productsimilarityservice.application.service;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.in.GetSimilarProducts;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class GetSimilarProductsImpl implements GetSimilarProducts {

    private final SimilarIdsClient similarIdsClient;
    private final ProductDetailClient productDetailClient;

    public GetSimilarProductsImpl(SimilarIdsClient similarIdsClient,
                                  ProductDetailClient productDetailClient) {
        this.similarIdsClient = similarIdsClient;
        this.productDetailClient = productDetailClient;
    }

    @Override
    @CircuitBreaker(name = "productSimilarityService", fallbackMethod = "fallbackSimilar")
    public List<Product> execute(String productId) {
        // 1. Obtener lista de IDs similares
        List<String> similarIds = similarIdsClient.fetchSimilarIds(productId);

        // 2. Por cada ID, obtener el detalle y mapear a domain.Product
        return similarIds.stream()
                .map(productDetailClient::fetchById)
                .collect(Collectors.toList());
    }

    public List<Product> fallbackSimilar(String productId, Throwable t) {
        // Graceful degradation
        return Collections.emptyList();
    }
}
