package com.example.productsimilarityservice.application.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.in.GetSimilarProducts;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetSimilarProductsImpl implements GetSimilarProducts {

    private static final Logger log = LoggerFactory.getLogger(GetSimilarProductsImpl.class);

    private final SimilarIdsClient similarIdsClient;
    private final ProductDetailClient productDetailClient;
    private final CircuitBreaker productCB;

    public GetSimilarProductsImpl(SimilarIdsClient similarIdsClient,
                                  ProductDetailClient productDetailClient,
                                  CircuitBreakerRegistry registry) {
        this.similarIdsClient = similarIdsClient;
        this.productDetailClient = productDetailClient;
        this.productCB = registry.circuitBreaker("productSimilarityService");
    }

    @Override
    public List<Product> execute(String productId) {
        List<String> similarIds;
        try {
          similarIds = similarIdsClient.fetchSimilarIds(productId);
        } catch (Throwable t) {
          return fallbackSimilar(productId, t);
        }
      
        return Flux.fromIterable(similarIds)
          .flatMap( id -> Mono
              .fromCallable(() -> productDetailClient.fetchById(id))
              .timeout(Duration.ofSeconds(3))
              .transformDeferred(CircuitBreakerOperator.of(productCB))
              .onErrorResume(ex -> {
                log.warn("ignoro fallo en {}: {}", id, ex.toString());
                return Mono.empty();
              })
          , 20)
          .collectList()
          .block();
    }

    // fallback global si ni siquiera podemos obtener la lista de IDs
    public List<Product> fallbackSimilar(String productId, Throwable t) {
        log.info("↩️ fallbackSimilar invoked for {}: {}", productId, t.toString());
        return Collections.emptyList();
    }
}
