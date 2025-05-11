package com.example.productsimilarityservice.application.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.productsimilarityservice.domain.model.Product;
import com.example.productsimilarityservice.domain.port.out.ProductDetailClient;
import com.example.productsimilarityservice.domain.port.out.SimilarIdsClient;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

class GetSimilarProductsPartialTest {

  SimilarIdsClient similarIdsClient;
  ProductDetailClient productDetailClient;
  GetSimilarProductsImpl useCase;

  @BeforeEach
  void init() {
    // 1) Mocks for the two ports
    similarIdsClient    = mock(SimilarIdsClient.class);
    productDetailClient = mock(ProductDetailClient.class);

    // 2) A real CircuitBreakerRegistry (closed by default)
    CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();

    // 3) Manual construction
    useCase = new GetSimilarProductsImpl(
      similarIdsClient,
      productDetailClient,
      registry
    );
  }

  @Test
  void execute_ignoresSingleDetailFailure_andReturnsOthers() {
    // arrange
    when(similarIdsClient.fetchSimilarIds("1"))
      .thenReturn(List.of("2", "100", "3"));

    Product p2 = new Product("2","Dress",19.99,true);
    when(productDetailClient.fetchById("2")).thenReturn(p2);

    when(productDetailClient.fetchById("100"))
      .thenThrow(new RuntimeException("fail100"));

    Product p3 = new Product("3","Blazer",29.99,false);
    when(productDetailClient.fetchById("3")).thenReturn(p3);

    // act
    List<Product> result = useCase.execute("1");

    // assert — only the good ones survive
    assertThat(result).containsExactly(p2, p3);
  }
}