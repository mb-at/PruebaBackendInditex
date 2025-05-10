package com.example.productsimilarityservice.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.in.GetSimilarProducts;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class GetSimilarProductsImpl implements GetSimilarProducts {

    private static final Logger log = LoggerFactory.getLogger(GetSimilarProductsImpl.class);

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
        List<String> similarIds = similarIdsClient.fetchSimilarIds(productId);

        return similarIds.stream()
                .map(this::fetchDetailSafely)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Product> fetchDetailSafely(String id) {
        try {
            return Optional.ofNullable(productDetailClient.fetchById(id));
        } catch (Exception ex) {
            log.warn("No pude obtener detalle para productId={}, lo ignoro. Causa: {}", id, ex.toString());
            log.debug("Stacktrace al ignorar fallo en productId=" + id, ex);
            return Optional.empty();
        }
    }

    // Fallback general si fetchSimilarIds falla o circuito abierto
    public List<Product> fallbackSimilar(String productId, Throwable t) {
        return Collections.emptyList();
    }
}
